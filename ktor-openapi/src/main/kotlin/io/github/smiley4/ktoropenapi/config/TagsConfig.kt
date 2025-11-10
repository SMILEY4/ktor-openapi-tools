package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.TagData
import io.github.smiley4.ktoropenapi.data.TagGroupData
import io.github.smiley4.ktoropenapi.data.TagsData

/**
 * Configuration for tags
 */
@OpenApiDslMarker
class TagsConfig internal constructor() {

    private val tags = mutableListOf<TagConfig>()
    private val tagGroups = mutableListOf<TagGroupConfig>()


    /**
     * Tags used by the specification with additional metadata. Not all tags that are used must be declared
     */
    fun tag(name: String, block: TagConfig.() -> Unit) {
        tags.add(TagConfig(name).apply(block))
    }


    /**
     * Define a tag group for organizing tags in API documentation (x-tagGroups vendor extension).
     * Tag groups are particularly useful for tools like Redoc to organize tags in the sidebar.
     *
     * Important: All tags used in your API should be included in a tag group, as tags not in any group may not be displayed.
     *
     * Example:
     * ```
     * tagGroup("User Management") {
     *     tag("Users")
     *     tag("API keys")
     *     tag("Admin")
     * }
     * ```
     */
    fun tagGroup(name: String, block: TagGroupConfig.() -> Unit) {
        tagGroups.add(TagGroupConfig(name).apply(block))
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
        tagGroups = buildList {
            addAll(base.tagGroups)
            addAll(tagGroups.map { it.build(TagGroupData.DEFAULT) })
        }
    )

}
