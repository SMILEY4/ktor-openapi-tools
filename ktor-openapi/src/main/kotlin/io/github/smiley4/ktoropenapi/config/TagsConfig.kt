package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.TagData
import io.github.smiley4.ktoropenapi.data.TagsData

/**
 * Configuration for tags
 */
@OpenApiDslMarker
class TagsConfig internal constructor() {

    private val tags = mutableListOf<TagConfig>()


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: TagConfig.() -> Unit) {
        tags.add(TagConfig(name).apply(block))
    }


    /**
     * Automatically add tags to the route with the given url.
     * The returned (non-null) tags will be added to the tags specified in the route-specific documentation.
     */
    var tagGenerator: TagGenerator = TagsData.DEFAULT.generator

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: TagsData) = TagsData(
        tags = buildList {
            addAll(base.tags)
            addAll(tags.map { it.build(TagData.DEFAULT) })
        },
        generator = merge(base.generator, tagGenerator) ?: TagsData.DEFAULT.generator,
    )

}
