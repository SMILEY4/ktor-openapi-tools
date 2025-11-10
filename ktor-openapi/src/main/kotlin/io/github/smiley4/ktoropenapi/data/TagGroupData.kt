package io.github.smiley4.ktoropenapi.data

/**
 * Represents a tag group for the x-tagGroups OpenAPI vendor extension.
 * Used to organize tags into logical groups in API documentation (e.g., Redoc).
 * See [Redocly x-tagGroups Documentation](https://redocly.com/docs/api-reference-docs/specification-extensions/x-tag-groups/).
 */
internal data class TagGroupData(
    val name: String,
    val tags: List<String>
) {

    companion object {
        val DEFAULT = TagGroupData(
            name = "",
            tags = emptyList()
        )
    }
}
