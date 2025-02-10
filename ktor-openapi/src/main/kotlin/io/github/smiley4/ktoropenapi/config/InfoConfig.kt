package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.ContactData
import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.DataUtils.mergeDefault
import io.github.smiley4.ktoropenapi.data.InfoData
import io.github.smiley4.ktoropenapi.data.LicenseData

/**
 * Basic information for the exposed API.
 */
@OpenApiDslMarker
class InfoConfig internal constructor() {

    /**
     * The title of the api
     */
    var title: String = "API"


    /**
     * The version of the OpenAPI document
     */
    var version: String? = "latest"


    /**
     * A short description of the API
     */
    var description: String? = null

    /**
     * A short summary of the API
     */
    var summary: String? = null


    /**
     * A URL to the Terms of Service for the API. MUST be in the format of a URL.
     */
    var termsOfService: String? = null

    private var contact: ContactConfig? = null


    /**
     * The contact information for the exposed API.
     */
    fun contact(block: ContactConfig.() -> Unit) {
        contact = ContactConfig().apply(block)
    }


    private var license: LicenseConfig? = null


    /**
     * The license information for the exposed API.
     */
    fun license(block: LicenseConfig.() -> Unit) {
        license = LicenseConfig().apply(block)
    }

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: InfoData): InfoData {
        return InfoData(
            title = mergeDefault(base.title, this.title, InfoData.DEFAULT.title),
            version = merge(base.version, this.version),
            description = merge(base.description, this.description),
            termsOfService = merge(base.termsOfService, this.termsOfService),
            contact = contact?.build(base.contact ?: ContactData.DEFAULT) ?: base.contact,
            license = license?.build(base.license ?: LicenseData.DEFAULT) ?: base.license,
            summary = merge(base.summary, this.summary)
        )
    }

}
