package io.github.smiley4.ktorswaggerui.builder.route

import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.dsl.routing.DocumentedRouteSelector
import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute
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
 * Collect all routes from the given application
 */
class RouteCollector(
    private val routeDocumentationMerger: RouteDocumentationMerger
) {

    /**
     * Collect all routes from the given application
     */
    fun collectRoutes(routeProvider: () -> RoutingNode, config: PluginConfigData): Sequence<RouteMeta> {
        return allRoutes(routeProvider())
            .asSequence()
            .map { route ->
                val documentation = getDocumentation(route, OpenApiRoute())
                RouteMeta(
                    method = getMethod(route),
                    path = getPath(route, config),
                    documentation = documentation.build(),
                    protected = documentation.protected ?: isProtected(route)
                )
            }
            .filter { !it.documentation.hidden }
            .filter { path -> config.pathFilter(path.method, path.path.split("/").filter { it.isNotEmpty() }) }
    }


    private fun getDocumentation(route: RoutingNode, base: OpenApiRoute): OpenApiRoute {
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
    private fun getPath(route: RoutingNode, config: PluginConfigData): String {
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


    private fun isIgnoredSelector(selector: RouteSelector, config: PluginConfigData): Boolean {
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
