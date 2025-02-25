package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.ContactData
import io.swagger.v3.oas.models.info.Contact

/**
 * Builds the openapi [Contact]-object. Holds Contact information for the exposed API.
 * See [OpenAPI Specification - Contact Object](https://swagger.io/specification/#contact-object).
 */
internal class ContactBuilder {

    fun build(contact: ContactData): Contact =
        Contact().also {
            it.name = contact.name
            it.email = contact.email
            it.url = contact.url
        }

}
