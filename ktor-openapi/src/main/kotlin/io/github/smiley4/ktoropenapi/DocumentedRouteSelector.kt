package io.github.smiley4.ktoropenapi

import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.resources.handle
import io.ktor.http.HttpMethod
import io.ktor.server.resources.resource
import io.ktor.server.routing.Route
import io.ktor.server.routing.RouteSelector
import io.ktor.server.routing.RouteSelectorEvaluation
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.RoutingResolveContext
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.head
import io.ktor.server.routing.method
import io.ktor.server.routing.options
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.utils.io.KtorDsl

class DocumentedRouteSelector(val documentation: RouteConfig) : RouteSelector() {

    companion object {
        private var includeDocumentedRouteInRouteToString = false
        fun setIncludeDocumentedRouteInRouteToString(include: Boolean) {
            includeDocumentedRouteInRouteToString = include
        }
    }

    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent

    override fun toString() = if (includeDocumentedRouteInRouteToString) super.toString() else ""
}

@KtorDsl
fun Route.documentation(
    documentation: RouteConfig.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    val documentedRoute = createChild(DocumentedRouteSelector(RouteConfig().apply(documentation)))
    documentedRoute.build()
    return documentedRoute
}

//============================//
//           ROUTING          //
//============================//

@KtorDsl
fun Route.route(
    builder: RouteConfig.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route("", build) }
}

@KtorDsl
fun Route.route(
    method: HttpMethod,
    builder: RouteConfig.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route("", method, build) }
}

@KtorDsl
fun Route.route(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route(path, build) }
}

@KtorDsl
fun Route.route(
    path: String,
    method: HttpMethod,
    builder: RouteConfig.() -> Unit = { },
    build: Route.() -> Unit
): Route {
    return documentation(builder) { route(path, method, build) }
}

@KtorDsl
fun Route.method(
    method: HttpMethod,
    builder: RouteConfig.() -> Unit = { },
    body: Route.() -> Unit
): Route {
    return documentation(builder) { method(method, body) }
}

//============================//
//             GET            //
//============================//

@KtorDsl
fun Route.get(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { get(path, body) }
}

@KtorDsl
fun Route.get(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { get(body) }
}


//============================//
//            POST            //
//============================//

@KtorDsl
fun Route.post(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { post(path, body) }
}

@KtorDsl
@JvmName("postTyped")
inline fun <reified R : Any> Route.post(
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { post(body) }
}

@KtorDsl
@JvmName("postTypedPath")
inline fun <reified R : Any> Route.post(
    path: String,
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { post(path, body) }
}

@KtorDsl
fun Route.post(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { post(body) }
}


//============================//
//             PUT            //
//============================//

@KtorDsl
fun Route.put(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { put(path, body) }
}

@KtorDsl
fun Route.put(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { put(body) }
}

@KtorDsl
@JvmName("putTyped")
inline fun <reified R : Any> Route.put(
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { put(body) }
}

@KtorDsl
@JvmName("putTypedPath")
inline fun <reified R : Any> Route.put(
    path: String,
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { put(path, body) }
}


//============================//
//           DELETE           //
//============================//

@KtorDsl
fun Route.delete(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { delete(path, body) }
}

@KtorDsl
fun Route.delete(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { delete(body) }
}


//============================//
//            PATCH           //
//============================//

@KtorDsl
fun Route.patch(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { patch(path, body) }
}

@KtorDsl
fun Route.patch(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { patch(body) }
}

@JvmName("patchTyped")
inline fun <reified R : Any> Route.patch(
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { patch(body) }

}

@JvmName("patchTypedPath")
inline fun <reified R : Any> Route.patch(
    path: String,
    noinline builder: RouteConfig.() -> Unit = { },
    crossinline body: suspend RoutingContext.(R) -> Unit
): Route {
    return documentation(builder) { patch(path, body) }
}


//============================//
//           OPTIONS          //
//============================//

@KtorDsl
fun Route.options(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { options(path, body) }
}

@KtorDsl
fun Route.options(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { options(body) }
}


//============================//
//            HEAD            //
//============================//

@KtorDsl
fun Route.head(
    path: String,
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { head(path, body) }
}

@KtorDsl
fun Route.head(
    builder: RouteConfig.() -> Unit = { },
    body: suspend RoutingContext.() -> Unit
): Route {
    return documentation(builder) { head(body) }
}


//===============================//
//            WEBHOOK            //
//===============================//

internal val webhooks = mutableMapOf<String, Pair<HttpMethod, RouteConfig>>()

fun webhook(method: HttpMethod, name: String, builder: RouteConfig.() -> Unit = { },) {
    webhooks[name] = method to RouteConfig().apply(builder)
}
