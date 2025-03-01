package io.github.smiley4.ktoropenapi.builder.openapi

import io.swagger.v3.oas.models.ExternalDocumentation

/**
 * Build the openapi [ExternalDocumentation]-object for a tag.
 * See [OpenAPI Specification - External Documentation Object](https://swagger.io/specification/#external-documentation-object).
 */
internal class TagExternalDocumentationBuilder {

    fun build(url: String, description: String): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = url
            it.description = description
        }
}
