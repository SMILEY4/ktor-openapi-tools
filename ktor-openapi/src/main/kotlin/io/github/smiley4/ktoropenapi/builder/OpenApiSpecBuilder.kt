package io.github.smiley4.ktoropenapi.builder

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktoropenapi.builder.example.ExampleContext
import io.github.smiley4.ktoropenapi.builder.example.ExampleContextImpl
import io.github.smiley4.ktoropenapi.builder.openapi.ComponentsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ContactBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ContentBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.HeaderBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.InfoBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.LicenseBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OAuthFlowsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OpenApiBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OperationBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OperationTagsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ParameterBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.PathBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.PathsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.RequestBodyBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ResponseBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ResponsesBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.SecuritySchemesBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ServerBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.TagBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.TagExternalDocumentationBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.WebhooksBuilder
import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContextImpl
import io.github.smiley4.ktoropenapi.config.OpenApiVersion
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.swagger.v3.core.util.Json
import io.swagger.v3.core.util.Json31
import io.swagger.v3.core.util.Yaml
import io.swagger.v3.core.util.Yaml31
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Builds the final openapi-specs.
 */
internal class OpenApiSpecBuilder {

    private val logger = KotlinLogging.logger {}


    /**
     * Create the openapi-spec in json and yaml for the with the given routes and configuration
     * @return a map of "specName" to pairs ("api-spec", "json"|"yaml")
     */
    fun build(config: OpenApiPluginData, routes: List<RouteMeta>): Map<String, Pair<String, OutputFormat>> {
        val routesBySpec = buildMap<String, MutableList<RouteMeta>> {
            routes.forEach { route ->
                val specName =
                    route.documentation.specName ?: config.specAssigner(route.path, route.documentation.tags.toList())
                computeIfAbsent(specName) { mutableListOf() }.add(route)
            }
        }
        return buildMap {
            routesBySpec.forEach { (specName, routes) ->
                val specConfig = config.specConfigs[specName] ?: config
                this[specName] = buildOpenApiSpec(specName, specConfig, routes)
            }
        }
    }

    private fun buildOpenApiSpec(specName: String, pluginConfig: OpenApiPluginData, routes: List<RouteMeta>): Pair<String, OutputFormat> {
        return try {
            val schemaContext = SchemaContextImpl(pluginConfig.schemaConfig).also {
                it.addGlobal(pluginConfig.schemaConfig)
                it.add(routes)
            }
            val exampleContext = ExampleContextImpl(pluginConfig.exampleConfig.exampleEncoder).also {
                it.addShared(pluginConfig.exampleConfig)
                it.add(routes)
            }
            val openApi = builder(pluginConfig, schemaContext, exampleContext).build(routes)
            pluginConfig.postBuild?.let { it(openApi, specName) }
            if (pluginConfig.openApiVersion == OpenApiVersion.V3_0) {
                transformSchemasTo30(openApi)
            }
            val spec30 = pluginConfig.openApiVersion == OpenApiVersion.V3_0
            when (pluginConfig.outputFormat) {
                OutputFormat.JSON -> (if (spec30) Json.pretty(openApi) else Json31.pretty(openApi)) to pluginConfig.outputFormat
                OutputFormat.YAML -> (if (spec30) Yaml.pretty(openApi) else Yaml31.pretty(openApi)) to pluginConfig.outputFormat
            }
        } catch (e: Exception) {
            logger.error(e) { "Error during openapi-spec generation" }
            return pluginConfig.outputFormat.empty to pluginConfig.outputFormat
        }
    }

    /**
     * Transforms all schemas in the given [OpenAPI] object from OpenAPI 3.1 format to 3.0 format.
     * Handles the following differences:
     * - Nullable: 3.1 uses a type set `types: {"string", "null"}`, 3.0 uses `type: "string", nullable: true`
     * - Examples: 3.1 uses `examples: [val]` on schemas, 3.0 uses a single `example: val`
     * - Exclusive bounds: 3.1 uses numeric `exclusiveMinimum`/`exclusiveMaximum`, 3.0 uses boolean flags alongside `minimum`/`maximum`
     * - Content encoding: 3.1 uses `contentEncoding`/`contentMediaType`, 3.0 uses `format: "byte"`/`format: "binary"`
     */
    private fun transformSchemasTo30(openApi: OpenAPI) {
        val visited = mutableSetOf<Schema<*>>()
        openApi.components?.schemas?.values?.forEach { transformSchema(it, visited) }
        openApi.paths?.values?.forEach { it.readOperations().forEach { op -> transformOperationSchemas(op, visited) } }
        openApi.webhooks?.values?.forEach { it.readOperations().forEach { op -> transformOperationSchemas(op, visited) } }
    }

    private fun transformOperationSchemas(operation: Operation, visited: MutableSet<Schema<*>>) {
        operation.parameters?.forEach { it.schema?.let { s -> transformSchema(s, visited) } }
        operation.requestBody?.content?.values?.forEach { it.schema?.let { s -> transformSchema(s, visited) } }
        operation.responses?.values?.forEach { transformResponseSchemas(it, visited) }
    }

    private fun transformResponseSchemas(response: ApiResponse, visited: MutableSet<Schema<*>>) {
        response.content?.values?.forEach { it.schema?.let { s -> transformSchema(s, visited) } }
        response.headers?.values?.forEach { it.schema?.let { s -> transformSchema(s, visited) } }
    }

    private fun transformSchema(schema: Schema<*>, visited: MutableSet<Schema<*>>) {
        if (!visited.add(schema)) return
        transformSchemaTypes(schema)
        transformSchemaExamples(schema)
        transformSchemaExclusiveBounds(schema)
        transformSchemaContentEncoding(schema)
        schema.properties?.values?.forEach { transformSchema(it, visited) }
        schema.allOf?.forEach { transformSchema(it, visited) }
        schema.anyOf?.forEach { transformSchema(it, visited) }
        schema.oneOf?.forEach { transformSchema(it, visited) }
        schema.not?.let { transformSchema(it, visited) }
        schema.items?.let { transformSchema(it, visited) }
        if (schema.additionalProperties is Schema<*>) {
            transformSchema(schema.additionalProperties as Schema<*>, visited)
        }
    }

    private fun transformSchemaTypes(schema: Schema<*>) {
        val types = schema.types ?: return
        if (types.isEmpty()) return
        val nonNullTypes = types.filter { it != "null" }
        val hasNull = types.contains("null")
        when {
            nonNullTypes.size == 1 -> {
                schema.type = nonNullTypes.first()
                if (hasNull) schema.nullable = true
            }
            nonNullTypes.isEmpty() -> schema.nullable = true
            else -> {
                // multiple non-null types: convert to anyOf
                val anyOf = nonNullTypes.map { t -> Schema<Any>().also { it.type = t } }
                schema.anyOf = (schema.anyOf ?: emptyList()) + anyOf
                if (hasNull) schema.nullable = true
            }
        }
        schema.types = null
    }

    /**
     * Transforms 3.1 schema-level `examples` (list) to a single 3.0 `example`.
     * Takes the first element of the list and discards the rest.
     */
    @Suppress("UNCHECKED_CAST")
    private fun transformSchemaExamples(schema: Schema<*>) {
        val examples = schema.examples ?: return
        if (examples.isEmpty()) return
        if (schema.example == null) {
            (schema as Schema<Any>).example = examples.first()
        }
        schema.examples = null
    }

    /**
     * Transforms 3.1 numeric `exclusiveMinimum`/`exclusiveMaximum` to 3.0 boolean flags.
     * - 3.1: `exclusiveMinimum: 7` (a number)
     * - 3.0: `minimum: 7, exclusiveMinimum: true`
     */
    private fun transformSchemaExclusiveBounds(schema: Schema<*>) {
        schema.exclusiveMinimumValue?.let { value ->
            if (schema.minimum == null) schema.minimum = value
            schema.exclusiveMinimum = true
            schema.exclusiveMinimumValue = null
        }
        schema.exclusiveMaximumValue?.let { value ->
            if (schema.maximum == null) schema.maximum = value
            schema.exclusiveMaximum = true
            schema.exclusiveMaximumValue = null
        }
    }

    /**
     * Transforms 3.1 `contentEncoding`/`contentMediaType` to 3.0 `format`.
     * - `contentEncoding: "base64"` → `format: "byte"`
     * - `contentMediaType: <any>` → `format: "binary"`
     */
    private fun transformSchemaContentEncoding(schema: Schema<*>) {
        schema.contentEncoding?.let { encoding ->
            if (schema.format == null) {
                schema.format = when (encoding.lowercase()) {
                    "base64", "base64url" -> "byte"
                    else -> null
                }
            }
            schema.contentEncoding = null
        }
        schema.contentMediaType?.let {
            if (schema.format == null) {
                schema.format = "binary"
            }
            schema.contentMediaType = null
        }
    }

    private fun builder(
        config: OpenApiPluginData,
        schemaContext: SchemaContext,
        exampleContext: ExampleContext,
    ): OpenApiBuilder {
        val pathBuilder = PathBuilder(
            operationBuilder = OperationBuilder(
                operationTagsBuilder = OperationTagsBuilder(config),
                parameterBuilder = ParameterBuilder(
                    schemaContext = schemaContext,
                    exampleContext = exampleContext,
                ),
                requestBodyBuilder = RequestBodyBuilder(
                    contentBuilder = ContentBuilder(
                        schemaContext = schemaContext,
                        exampleContext = exampleContext,
                        headerBuilder = HeaderBuilder(schemaContext)
                    )
                ),
                responsesBuilder = ResponsesBuilder(
                    responseBuilder = ResponseBuilder(
                        headerBuilder = HeaderBuilder(schemaContext),
                        contentBuilder = ContentBuilder(
                            schemaContext = schemaContext,
                            exampleContext = exampleContext,
                            headerBuilder = HeaderBuilder(schemaContext)
                        )
                    ),
                    config = config
                ),
                securityRequirementsBuilder = SecurityRequirementsBuilder(config),
                externalDocumentationBuilder = ExternalDocumentationBuilder(),
                serverBuilder = ServerBuilder()
            )
        )
        return OpenApiBuilder(
            config = config,
            schemaContext = schemaContext,
            exampleContext = exampleContext,
            infoBuilder = InfoBuilder(
                contactBuilder = ContactBuilder(),
                licenseBuilder = LicenseBuilder()
            ),
            externalDocumentationBuilder = ExternalDocumentationBuilder(),
            serverBuilder = ServerBuilder(),
            tagBuilder = TagBuilder(
                tagExternalDocumentationBuilder = TagExternalDocumentationBuilder()
            ),
            pathsBuilder = PathsBuilder(
                config = config,
                pathBuilder = pathBuilder
            ),
            webhooksBuilder = WebhooksBuilder(
                pathBuilder = pathBuilder
            ),
            componentsBuilder = ComponentsBuilder(
                config = config,
                securitySchemesBuilder = SecuritySchemesBuilder(
                    oAuthFlowsBuilder = OAuthFlowsBuilder()
                )
            )
        )
    }
}
