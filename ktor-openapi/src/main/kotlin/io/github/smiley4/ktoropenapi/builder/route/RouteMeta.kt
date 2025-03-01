package io.github.smiley4.ktoropenapi.builder.route

import io.github.smiley4.ktoropenapi.data.RouteData
import io.ktor.http.HttpMethod

/**
 * Information about a route
 */
internal data class RouteMeta(
    val path: String,
    val method: HttpMethod,
    val documentation: RouteData,
    val protected: Boolean,
    val isWebhook: Boolean,
)
