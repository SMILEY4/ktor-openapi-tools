package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - License Object](https://swagger.io/specification/#license-object).
 */
internal data class LicenseData(
    val name: String?,
    val url: String?,
    val identifier: String?
) {
    companion object {
        val DEFAULT = LicenseData(
            name = null,
            url = null,
            identifier = null
        )
    }
}
