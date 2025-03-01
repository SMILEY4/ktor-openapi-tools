package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.TagData
import io.swagger.v3.oas.models.tags.Tag

/**
 * Build the openapi [Tag]-object. Holds metadata of a single tag.
 * See [OpenAPI Specification - Tag Object](https://swagger.io/specification/#tag-object).
 */
internal class TagBuilder(
    private val tagExternalDocumentationBuilder: TagExternalDocumentationBuilder
) {

    fun build(tag: TagData): Tag =
        Tag().also {
            it.name = tag.name
            it.description = tag.description
            if(tag.externalDocUrl != null && tag.externalDocDescription != null) {
                it.externalDocs = tagExternalDocumentationBuilder.build(tag.externalDocUrl, tag.externalDocDescription)
            }
        }

}
