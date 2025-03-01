package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.github.smiley4.ktoropenapi.builder.route.RouteMeta

/**
 * Builds the list of tags for a single route.
 */
internal class OperationTagsBuilder(
    private val config: OpenApiPluginData
) {

    fun build(route: RouteMeta): List<String> {
        return mutableSetOf<String?>().also { tags ->
            tags.addAll(getGeneratedTags(route))
            tags.addAll(getRouteTags(route))
        }.filterNotNull()
    }

    private fun getRouteTags(route: RouteMeta) = route.documentation.tags

    private fun getGeneratedTags(route: RouteMeta) = config.tagsConfig.generator(route.path.split("/").filter { it.isNotEmpty() })

}
