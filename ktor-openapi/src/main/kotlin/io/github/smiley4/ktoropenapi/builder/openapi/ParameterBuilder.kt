package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.example.ExampleContext
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.github.smiley4.ktoropenapi.data.RequestParameterData
import io.github.smiley4.ktoropenapi.config.ParameterLocation
import io.swagger.v3.oas.models.parameters.Parameter

/**
 * Build the openapi [Parameter]-object. Holds information describing a single operation (query, path or header) parameter.
 * See [OpenAPI Specification - Parameter Object](https://swagger.io/specification/#parameter-object).
 */
internal class ParameterBuilder(
    private val schemaContext: SchemaContext,
    private val exampleContext: ExampleContext
) {

    fun build(parameter: RequestParameterData): Parameter =
        Parameter().also {
            it.`in` = when (parameter.location) {
                ParameterLocation.QUERY -> "query"
                ParameterLocation.HEADER -> "header"
                ParameterLocation.PATH -> "path"
                ParameterLocation.COOKIE -> "cookie"
            }
            it.name = parameter.name
            it.description = parameter.description
            it.required = parameter.required
            it.deprecated = parameter.deprecated
            it.allowEmptyValue = parameter.allowEmptyValue
            it.explode = parameter.explode
            it.example = parameter.example?.let { e -> exampleContext.getExample(e).value }
            it.allowReserved = parameter.allowReserved
            it.schema = schemaContext.getSchema(parameter.type)
            it.style = parameter.style
        }

}
