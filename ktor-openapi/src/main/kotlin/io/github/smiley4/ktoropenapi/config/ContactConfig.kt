package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.ContactData
import io.github.smiley4.ktoropenapi.data.DataUtils.merge

/**
 * Contact information for the exposed API.
 */
@OpenApiDslMarker
class ContactConfig internal constructor() {

    /**
     * The identifying name of the contact person/organization.
     */
    var name: String? = ContactData.DEFAULT.name


    /**
     * The URL pointing to the contact information. MUST be in the format of a URL.
     */
    var url: String? = ContactData.DEFAULT.url


    /**
     * The email address of the contact person/organization. MUST be in the format of an email address.
     */
    var email: String? = ContactData.DEFAULT.email


    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: ContactData) = ContactData(
        name = merge(base.name, name),
        url = merge(base.url, url),
        email = merge(base.email, email)
    )

}
