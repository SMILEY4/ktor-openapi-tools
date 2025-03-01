package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.BaseBodyData
import io.swagger.v3.oas.models.parameters.RequestBody

/**
 * Build the openapi [RequestBody]-object. Holds information describing a single request body.
 * See [OpenAPI Specification - Request Body Object](https://swagger.io/specification/#request-body-object).
 */
internal class RequestBodyBuilder(
    private val contentBuilder: ContentBuilder
) {

    fun build(body: BaseBodyData): RequestBody =
        RequestBody().also {
            it.description = body.description
            it.required = body.required
            it.content = contentBuilder.build(body)
        }

}
