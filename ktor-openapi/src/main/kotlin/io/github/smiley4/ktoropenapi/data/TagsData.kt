package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.TagGenerator

/**
 * Common configuration for tags.
 */
internal data class TagsData(
    val tags: List<TagData>,
    val generator: TagGenerator,
) {

    companion object {
        val DEFAULT = TagsData(
            tags = emptyList(),
            generator = { emptyList() }
        )
    }

}
