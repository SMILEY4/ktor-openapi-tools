package io.github.smiley4.ktoropenapi.builder.route

import io.github.smiley4.ktoropenapi.DocumentedRouteSelector
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.ktor.http.HttpMethod
import io.ktor.server.auth.AuthenticationRouteSelector
import io.ktor.server.routing.ConstantParameterRouteSelector
import io.ktor.server.routing.HttpMethodRouteSelector
import io.ktor.server.routing.OptionalParameterRouteSelector
import io.ktor.server.routing.ParameterRouteSelector
import io.ktor.server.routing.RootRouteSelector
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RoutingNode
import io.ktor.server.routing.TrailingSlashRouteSelector
import kotlin.reflect.full.isSubclassOf

/**
 * Collect all routes of the given application.
 */
internal class RouteCollector {

    private val routeDocumentationMerger = RouteDocumentationMerger()

    private val hiddenRouteMarkers = setOf(
        "io.github.smiley4.ktorswaggerui.SwaggerUIRouteSelector",
        "io.github.smiley4.ktorredoc.RedocRouteSelector"
    )


    /**
     * Collect all routes from the given application
     */
    fun collect(routeProvider: () -> RoutingNode, config: OpenApiPluginData): List<RouteMeta> {
        return allRoutes(routeProvider())
            .asSequence()
            .map { route ->
                val documentation = getDocumentation(route, RouteConfig())
                RouteMeta(
                    method = getMethod(route),
                    path = getPath(route, config),
                    documentation = documentation.build(),
                    protected = documentation.protected ?: isProtected(route),
                    isWebhook = false
                )
            }
            .filter { !it.documentation.hidden }
            .filter { path -> hiddenRouteMarkers.none { path.path.contains(it) } }
            .filter { path -> config.pathFilter(path.method, path.path.split("/").filter { it.isNotEmpty() }) }
            .toList()
    }

    private fun unroll(route: RoutingNode): List<Pair<RoutingNode, RouteSelector>> {
        return if (route.parent != null) {
            unroll(route.parent!!) + listOf(route to route.selector)
        } else {
            emptyList()
        }
    }

    private fun getDocumentation(route: RoutingNode, base: RouteConfig): RouteConfig {
        var documentation = base
        if (route.selector is DocumentedRouteSelector) {
            documentation = routeDocumentationMerger.merge(documentation, (route.selector as DocumentedRouteSelector).documentation)
        }
        return if (route.parent != null) {
            getDocumentation(route.parent!!, documentation)
        } else {
            documentation
        }
    }


    private fun getMethod(route: RoutingNode): HttpMethod {
        return (route.selector as HttpMethodRouteSelector).method
    }


    @Suppress("CyclomaticComplexMethod")
    private fun getPath(route: RoutingNode, config: OpenApiPluginData): String {
        val selector = route.selector
        return if (isIgnoredSelector(selector, config)) {
            route.parent?.let { getPath(it, config) } ?: ""
        } else {
            when (route.selector) {
                is RootRouteSelector -> ""
                is TrailingSlashRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is DocumentedRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is HttpMethodRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is AuthenticationRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is ParameterRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is ConstantParameterRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                is OptionalParameterRouteSelector -> route.parent?.let { getPath(it, config) } ?: ""
                else -> (route.parent?.let { getPath(it, config) } ?: "").dropLastWhile { it == '/' } + "/" + route.selector.toString()
            }
        }
    }


    private fun isIgnoredSelector(selector: RouteSelector, config: OpenApiPluginData): Boolean {
        return when (selector) {
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> true
            is HttpMethodRouteSelector -> true
            is AuthenticationRouteSelector -> true
            is ParameterRouteSelector -> true
            is ConstantParameterRouteSelector -> true
            is OptionalParameterRouteSelector -> true
            else -> config.ignoredRouteSelectors.any { selector::class.isSubclassOf(it) } or
                    config.ignoredRouteSelectorClassNames.any { selector::class.java.name == it }
        }
    }


    private fun isProtected(route: RoutingNode): Boolean {
        return when (route.selector) {
            is AuthenticationRouteSelector -> true
            is TrailingSlashRouteSelector -> false
            is RootRouteSelector -> false
            is DocumentedRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            is HttpMethodRouteSelector -> route.parent?.let { isProtected(it) } ?: false
            else -> route.parent?.let { isProtected(it) } ?: false

        }
    }

    private fun allRoutes(root: RoutingNode): List<RoutingNode> {
        return (listOf(root) + root.children.flatMap { allRoutes(it) })
            .filter { it.selector is HttpMethodRouteSelector }
    }
}
