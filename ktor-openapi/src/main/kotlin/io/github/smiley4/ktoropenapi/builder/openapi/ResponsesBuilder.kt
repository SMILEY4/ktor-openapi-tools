package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.ResponseData
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.ktor.http.HttpStatusCode
import io.swagger.v3.oas.models.responses.ApiResponses

/**
 * Build the openapi [ApiResponses]-object. A container for the expected responses of an operation.
 * See [OpenAPI Specification - Responses Object](https://swagger.io/specification/#responses-object).
 */
internal class ResponsesBuilder(
    private val responseBuilder: ResponseBuilder,
    private val config: OpenApiPluginData
) {

    fun build(responses: List<ResponseData>, isProtected: Boolean): ApiResponses =
        ApiResponses().also {
            responses
                .filter { !it.hidden }
                .map { response -> responseBuilder.build(response) }
                .forEach { (name, response) -> it.addApiResponse(name, response) }
            if (shouldAddUnauthorized(responses, isProtected)) {
                config.securityConfig.defaultUnauthorizedResponse
                    ?.let { response -> responseBuilder.build(response) }
                    ?.also { (name, response) -> it.addApiResponse(name, response) }
            }
        }

    private fun shouldAddUnauthorized(responses: List<ResponseData>, isProtected: Boolean): Boolean {
        val unauthorizedCode = HttpStatusCode.Unauthorized.value.toString()
        return config.securityConfig.defaultUnauthorizedResponse != null
                && !config.securityConfig.defaultUnauthorizedResponse.hidden
                && isProtected
                && responses.count { it.statusCode == unauthorizedCode } == 0
    }

}
