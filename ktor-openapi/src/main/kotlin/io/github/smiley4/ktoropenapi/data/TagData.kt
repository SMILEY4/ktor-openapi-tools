package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - Tag Object](https://swagger.io/specification/#tag-object).
 */
internal data class TagData(
    val name: String,
    val description: String?,
    val externalDocDescription: String?,
    val externalDocUrl: String?,
    /**
     * Custom display name for the tag. Maps to Redoc's x-displayName extension.
     * When defined, this name is used instead of the default name in the navigation sidebar and section headings.
     */
    val displayName: String?
) {

    companion object {
        val DEFAULT = TagData(
            name = "",
            description = null,
            externalDocDescription = null,
            externalDocUrl = null,
            displayName = null
        )
    }
}
