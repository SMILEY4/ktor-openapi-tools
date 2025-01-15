package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.TagData
import io.github.smiley4.ktorswaggerui.data.TagGenerator
import io.github.smiley4.ktorswaggerui.data.TagsData
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker

/**
 * Configuration for tags
 */
@OpenApiDslMarker
class OpenApiTags {

    private val tags = mutableListOf<OpenApiTag>()


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: OpenApiTag.() -> Unit) {
        tags.add(OpenApiTag(name).apply(block))
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
    fun build(base: TagsData) = TagsData(
        tags = buildList {
            addAll(base.tags)
            addAll(tags.map { it.build(TagData.DEFAULT) })
        },
        generator = mergeDefault(base.generator, tagGenerator, TagsData.DEFAULT.generator)
    )

}
