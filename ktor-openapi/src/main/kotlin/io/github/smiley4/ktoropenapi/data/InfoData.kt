package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - Info Object](https://swagger.io/specification/#info-object).
 */
internal data class InfoData(
    val title: String,
    val version: String?,
    val description: String?,
    val summary: String?,
    val termsOfService: String?,
    val contact: ContactData?,
    val license: LicenseData?,
) {
    companion object {
        val DEFAULT = InfoData(
            title = "API",
            version = null,
            description = null,
            summary = null,
            termsOfService = null,
            contact = null,
            license = null,
        )
    }
}
