package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.ExternalDocsData
import io.swagger.v3.oas.models.ExternalDocumentation

/**
 * Build the openapi [ExternalDocumentation]-object. Allows referencing an external resource for extended documentation.
 * See [OpenAPI Specification - External Documentation Object](https://swagger.io/specification/#external-documentation-object).
 */
internal class ExternalDocumentationBuilder {

    fun build(externalDocs: ExternalDocsData): ExternalDocumentation =
        ExternalDocumentation().also {
            it.url = externalDocs.url
            it.description = externalDocs.description
        }

}

