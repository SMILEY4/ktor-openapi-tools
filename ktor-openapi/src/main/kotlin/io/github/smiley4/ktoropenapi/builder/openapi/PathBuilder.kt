package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.ktor.http.HttpMethod
import io.swagger.v3.oas.models.PathItem

/**
 * Build the openapi [PathItem]-object. Holds information describing the operations available on a single path.
 * See [OpenAPI Specification - Path Item Object](https://swagger.io/specification/#path-item-object).
 */
internal class PathBuilder(
    private val operationBuilder: OperationBuilder
) {

    fun build(route: RouteMeta): PathItem =
        PathItem().also {
            val operation = operationBuilder.build(route)
            when (route.method) {
                HttpMethod.Get -> it.get = operation
                HttpMethod.Post -> it.post = operation
                HttpMethod.Put -> it.put = operation
                HttpMethod.Patch -> it.patch = operation
                HttpMethod.Delete -> it.delete = operation
                HttpMethod.Head -> it.head = operation
                HttpMethod.Options -> it.options = operation
            }
        }

}
