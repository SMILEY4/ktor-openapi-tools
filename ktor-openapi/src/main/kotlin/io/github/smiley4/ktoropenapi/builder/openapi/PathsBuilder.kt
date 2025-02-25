package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths

/**
 * Build the openapi [Paths]-object. Holds the relative paths to the individual endpoints and their operations.
 * See [OpenAPI Specification - Paths Object](https://swagger.io/specification/#paths-object).
 */
internal class PathsBuilder(
    private val config: OpenApiPluginData,
    private val pathBuilder: PathBuilder
) {

    fun build(routes: Collection<RouteMeta>): Paths =
        Paths().also {
            routes.forEach { route ->
                val url = createUrl(route)
                val existingPath = it[url]
                if (existingPath != null) {
                    addToExistingPath(existingPath, route)
                } else {
                    addAsNewPath(url, it, route)
                }
            }
        }

    private fun createUrl(route: RouteMeta): String {
        return "${config.rootPath ?: ""}${route.path}"
    }

    private fun addAsNewPath(url: String, paths: Paths, route: RouteMeta) {
        paths.addPathItem(url, pathBuilder.build(route))
    }

    private fun addToExistingPath(existing: PathItem, route: RouteMeta) {
        val path = pathBuilder.build(route)
        existing.get = path.get ?: existing.get
        existing.put = path.put ?: existing.put
        existing.post = path.post ?: existing.post
        existing.delete = path.delete ?: existing.delete
        existing.options = path.options ?: existing.options
        existing.head = path.head ?: existing.head
        existing.patch = path.patch ?: existing.patch
        existing.trace = path.trace ?: existing.trace
    }

}
