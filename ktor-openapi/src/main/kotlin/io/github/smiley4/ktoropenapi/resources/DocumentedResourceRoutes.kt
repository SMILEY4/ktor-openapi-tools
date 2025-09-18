package io.github.smiley4.ktoropenapi.resources

import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.documentation
import io.ktor.server.resources.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingContext
import io.ktor.utils.io.KtorDsl
import kotlinx.serialization.serializer
import kotlin.reflect.typeOf

//============================//
//             GET            //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.get(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            get<T>(body)
        }
    }
}

//============================//
//            POST            //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.post(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            post<T>(body)
        }
    }
}

@KtorDsl
inline fun <reified T : Any, reified R : Any> Route.post(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T, R) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), typeOf<R>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            post<T, R>(body)
        }
    }
}

//============================//
//             PUT            //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.put(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            put<T>(body)
        }
    }
}

@KtorDsl
inline fun <reified T : Any, reified R : Any> Route.put(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T, R) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), typeOf<R>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            put<T, R>(body)
        }
    }
}

//============================//
//           DELETE           //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.delete(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            delete<T>(body)
        }
    }
}

//============================//
//            PATCH           //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.patch(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            patch<T>(body)
        }
    }
}

@KtorDsl
inline fun <reified T : Any, reified R : Any> Route.patch(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T, R) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), typeOf<R>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            patch<T, R>(body)
        }
    }
}


//============================//
//           OPTIONS          //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.options(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            options<T>(body)
        }
    }
}

//============================//
//            HEAD            //
//============================//

@KtorDsl
inline fun <reified T : Any> Route.head(
    noinline builder: RouteConfig.() -> Unit = { },
    noinline body: suspend RoutingContext.(T) -> Unit
): Route {
    val resources = plugin(Resources)
    val extractedDocumentation = extractTypesafeDocumentation(serializer<T>(), resources.resourcesFormat)
    return documentation(extractedDocumentation) {
        documentation(builder) {
            head<T>(body)
        }
    }
}
