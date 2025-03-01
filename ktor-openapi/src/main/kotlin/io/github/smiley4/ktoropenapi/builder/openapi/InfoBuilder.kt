package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.InfoData
import io.swagger.v3.oas.models.info.Info

/**
 * Build the openapi [Info]-object. Holds metadata about the API.
 * See [OpenAPI Specification - Info Object](https://swagger.io/specification/#info-object).
 */
internal class InfoBuilder(
    private val contactBuilder: ContactBuilder,
    private val licenseBuilder: LicenseBuilder
) {

    fun build(info: InfoData): Info =
        Info().also {
            it.title = info.title
            it.version = info.version
            it.description = info.description
            it.termsOfService = info.termsOfService
            info.contact?.also { contact ->
                it.contact = contactBuilder.build(contact)
            }
            info.license?.also { license ->
                it.license = licenseBuilder.build(license)
            }
            it.summary = info.summary
        }

}
