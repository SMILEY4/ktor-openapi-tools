package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils
import io.github.smiley4.ktoropenapi.data.ExternalDocsData

/**
 * An object representing external documentation.
 */
@OpenApiDslMarker
class ExternalDocsConfig internal constructor() {

    /**
     * A short description of the external documentation
     */
    var description: String? = null


    /**
     * A URL to the external documentation
     */
    var url: String = "/"

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: ExternalDocsData) = ExternalDocsData(
        url = DataUtils.mergeDefault(base.url, url, ExternalDocsData.DEFAULT.url),
        description = DataUtils.merge(base.description, description)
    )

}
