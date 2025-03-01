package io.github.smiley4.ktorswaggerui

import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingResolveContext

internal class SwaggerUIRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent
}

internal fun Route.markedSwaggerUI(build: Route.() -> Unit): Route {
    return createChild(SwaggerUIRouteSelector()).also(build)
}
