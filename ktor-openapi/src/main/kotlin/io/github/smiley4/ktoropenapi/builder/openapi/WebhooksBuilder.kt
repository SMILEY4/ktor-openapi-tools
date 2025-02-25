package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.swagger.v3.oas.models.PathItem

/**
 * Build the openapi "webhooks" section.
 * See [OpenAPI Specification - Webhooks](https://spec.openapis.org/oas/v3.1.0.html#oasWebhooks)
 */
internal class WebhooksBuilder(
    private val pathBuilder: PathBuilder
) {

    fun build(routes: Collection<RouteMeta>): Map<String, PathItem> {
        return routes.associate { route ->
            route.path to pathBuilder.build(route)
        }
    }

}
