package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.ResponseData
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Build the openapi [ApiResponse]-objects by status-code. Holds information describing status-codes and responses from an API Operation.
 * See [OpenAPI Specification - Response Object](https://swagger.io/specification/#response-object).
 */
internal class ResponseBuilder(
    private val headerBuilder: HeaderBuilder,
    private val contentBuilder: ContentBuilder
) {

    fun build(response: ResponseData): Pair<String, ApiResponse> =
        response.statusCode to ApiResponse().also {
            it.description = response.description
            it.headers = response.headers.mapValues { header -> headerBuilder.build(header.value) }
            response.body?.let { body ->
                it.content = contentBuilder.build(body)
            }
        }

}
