package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema

/**
 * Builds the openapi [Components]-object containing shared reusable schemas and examples.
 * See [OpenAPI Specification - Components Object](https://swagger.io/specification/#components-object).
 */
internal class ComponentsBuilder(
    private val config: OpenApiPluginData,
    private val securitySchemesBuilder: SecuritySchemesBuilder
) {

    fun build(schemas: Map<String, Schema<*>>, examples: Map<String, Example>): Components {
        return Components().also {
            it.schemas = schemas
            it.examples = examples
            if (config.securityConfig.securitySchemes.isNotEmpty()) {
                it.securitySchemes = securitySchemesBuilder.build(config.securityConfig.securitySchemes)
            }
        }
    }

}
