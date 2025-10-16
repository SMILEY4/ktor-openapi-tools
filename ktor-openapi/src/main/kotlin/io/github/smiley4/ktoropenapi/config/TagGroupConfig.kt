package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.TagGroupData

/**
 * Configuration for a tag group (x-tagGroups OpenAPI vendor extension).
 * Tag groups are used to organize tags into logical groups in API documentation.
 * See [Redocly x-tagGroups Documentation](https://redocly.com/docs/api-reference-docs/specification-extensions/x-tag-groups/).
 *
 * Important: All tags used in your API should be included in a tag group, as tags not in any group may not be displayed.
 */
@OpenApiDslMarker
class TagGroupConfig internal constructor(
    /**
     * The name of the tag group (e.g., "User Management", "Statistics").
     */
    var name: String
) {

    /**
     * List of tag names to include in this group.
     * These should match the tag names used in your API operations.
     */
    val tags = mutableListOf<String>()

    /**
     * Add a tag to this group.
     */
    fun tag(tagName: String) {
        tags.add(tagName)
    }

    internal fun build(base: TagGroupData) = TagGroupData(
        name = name,
        tags = buildList {
            addAll(base.tags)
            addAll(tags)
        }
    )

}
