package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - Tag Object](https://swagger.io/specification/#tag-object).
 */
internal data class TagData(
    val name: String,
    val description: String?,
    val externalDocDescription: String?,
    val externalDocUrl: String?
) {

    companion object {
        val DEFAULT = TagData(
            name = "",
            description = null,
            externalDocDescription = null,
            externalDocUrl = null
        )
    }
}
