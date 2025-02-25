package io.github.smiley4.ktorredoc

import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

internal class RedocRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}

internal fun Route.markedRedoc(build: Route.() -> Unit): Route {
    return createChild(RedocRouteSelector()).also(build)
}
