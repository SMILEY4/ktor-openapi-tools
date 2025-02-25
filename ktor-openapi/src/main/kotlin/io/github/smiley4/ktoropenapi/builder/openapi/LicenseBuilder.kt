package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.LicenseData
import io.swagger.v3.oas.models.info.License

/**
 * Build the openapi [License]-object. Holds license information for the exposed API.
 * See [OpenAPI Specification - License Object](https://swagger.io/specification/#license-object).
 */
internal class LicenseBuilder {

    fun build(license: LicenseData): License =
        License().also {
            it.name = license.name
            it.url = license.url
            it.identifier = license.identifier
        }

}
