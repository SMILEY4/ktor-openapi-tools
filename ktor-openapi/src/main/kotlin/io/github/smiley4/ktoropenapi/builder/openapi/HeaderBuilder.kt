package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.github.smiley4.ktoropenapi.data.HeaderData
import io.swagger.v3.oas.models.headers.Header

/**
 * Build the openapi [Header]-object.
 * See [OpenAPI Specification - Header Object](https://swagger.io/specification/#header-object).
 */
internal class HeaderBuilder(
    private val schemaContext: SchemaContext
) {

    fun build(header: HeaderData): Header =
        Header().also {
            it.description = header.description
            it.required = header.required
            it.deprecated = header.deprecated
            it.schema = header.type?.let { t -> schemaContext.getSchema(t) }
            it.explode = header.explode
//            it.example = TODO()
        }

}
