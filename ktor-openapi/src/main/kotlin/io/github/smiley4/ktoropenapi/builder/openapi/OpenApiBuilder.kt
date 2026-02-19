package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.example.ExampleContext
import io.github.smiley4.ktoropenapi.config.OpenApiVersion
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.SpecVersion

/**
 * Build the openapi [OpenAPI]-object. Is the root of the openapi document.
 * See [OpenAPI Specification - OpenAPI Object](https://swagger.io/specification/#openapi-object).
 */
internal class OpenApiBuilder(
    private val config: OpenApiPluginData,
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext,
    private val infoBuilder: InfoBuilder,
    private val externalDocumentationBuilder: ExternalDocumentationBuilder,
    private val serverBuilder: ServerBuilder,
    private val tagBuilder: TagBuilder,
    private val pathsBuilder: PathsBuilder,
    private val webhooksBuilder: WebhooksBuilder,
    private val componentsBuilder: ComponentsBuilder,
) {

    fun build(routes: Collection<RouteMeta>): OpenAPI {
        return OpenAPI().also {
            it.specVersion = if (config.openApiVersion == OpenApiVersion.V3_0) SpecVersion.V30 else SpecVersion.V31
            it.openapi = if (config.openApiVersion == OpenApiVersion.V3_0) "3.0.3" else "3.1.0"
            it.info = infoBuilder.build(config.info)
            it.externalDocs = externalDocumentationBuilder.build(config.externalDocs)
            it.servers = config.servers.map { server -> serverBuilder.build(server) }
            it.tags = config.tagsConfig.tags.map { tag -> tagBuilder.build(tag) }
            it.paths = pathsBuilder.build(routes.filter { r -> !r.isWebhook})
            it.webhooks = webhooksBuilder.build(routes.filter { r -> r.isWebhook})
            it.components = componentsBuilder.build(schemaContext.getComponentSection(), exampleContext.getComponentSection())

            // Add x-tagGroups vendor extension if tag groups are configured
            if (config.tagsConfig.tagGroups.isNotEmpty()) {
                it.addExtension("x-tagGroups", config.tagsConfig.tagGroups.map { tagGroup ->
                    mapOf(
                        "name" to tagGroup.name,
                        "tags" to tagGroup.tags
                    )
                })
            }
        }
    }

}
