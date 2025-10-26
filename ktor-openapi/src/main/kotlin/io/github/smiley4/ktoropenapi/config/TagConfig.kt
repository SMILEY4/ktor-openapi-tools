package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.TagData

/**
 * Adds metadata to a single tag.
 */
@OpenApiDslMarker
class TagConfig internal constructor(
    /**
     * The name of the tag.
     */
    var name: String
) {

    /**
     * A short description for the tag.
     */
    var description: String? = null

    /**
     * A short description of additional external documentation for this tag.
     */
    var externalDocDescription: String? = null

    /**
     * The URL for additional external documentation for this tag.
     */
    var externalDocUrl: String? = null

    /**
     * Custom display name for the tag. Maps to Redoc's x-displayName extension.
     * When defined, this name is used instead of the default name in the navigation sidebar and section headings.
     */
    var displayName: String? = null


    internal fun build(base: TagData) = TagData(
        name = name,
        description = merge(base.description, description),
        externalDocDescription = merge(base.externalDocDescription, externalDocDescription),
        externalDocUrl = merge(base.externalDocUrl, externalDocUrl),
        displayName = merge(base.displayName, displayName)
    )

}
