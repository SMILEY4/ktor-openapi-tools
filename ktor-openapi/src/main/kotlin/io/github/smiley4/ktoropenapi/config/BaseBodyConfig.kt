package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.BaseBodyData
import io.ktor.http.ContentType

/**
 * Describes a single request/response body with a single schema.
 */
@OpenApiDslMarker
sealed class BaseBodyConfig {

    /**
     * A brief description of the request body
     */
    var description: String? = null

    /**
     * Determines if the request body is required in the request
     */
    var required: Boolean? = null

    /**
     * Allowed Media Types for this body. If none specified, a media type will be chosen automatically based on the provided schema
     */
    var mediaTypes: Collection<ContentType> = emptySet()

    /**
     * Set the allowed Media Types for this body. If none specified, a media type will be chosen automatically based on the provided schema
     */
    fun mediaTypes(mediaTypes: Collection<ContentType>) {
        this.mediaTypes = mediaTypes
    }

    /**
     * Set the allowed Media Types for this body. If none specified, a media type will be chosen automatically based on the provided schema
     */
    fun mediaTypes(vararg mediaTypes: ContentType) {
        this.mediaTypes = mediaTypes.toList()
    }

    /**
     * Build the data object for this config.
     */
    internal abstract fun build(): BaseBodyData
}
