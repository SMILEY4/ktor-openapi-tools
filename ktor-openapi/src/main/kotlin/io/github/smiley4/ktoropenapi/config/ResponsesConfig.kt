package io.github.smiley4.ktoropenapi.config

import io.ktor.http.HttpStatusCode

/**
 * All possible responses of an operation
 */
@OpenApiDslMarker
class ResponsesConfig {

    private val responses = mutableMapOf<String, ResponseConfig>()


    /**
     * Information of response for a given http status code
     */
    infix fun String.to(block: ResponseConfig.() -> Unit) {
        responses[this] = ResponseConfig(this).apply(block)
    }


    /**
     * Information of response for a given http status code
     */
    infix fun HttpStatusCode.to(block: ResponseConfig.() -> Unit) = this.value.toString() to block

    /**
     * Information of response for a given http status code
     */
    fun code(statusCode: String, block: ResponseConfig.() -> Unit) {
        responses[statusCode] = ResponseConfig(statusCode).apply(block)
    }

    /**
     * Information of response for a given http status code
     */
    fun code(statusCode: HttpStatusCode, block: ResponseConfig.() -> Unit) = code(statusCode.value.toString(), block)


    /**
     * Information of the default response
     */
    fun default(block: ResponseConfig.() -> Unit) = "default" to block


    /**
     * Add the given response.
     */
    internal fun addResponse(response: ResponseConfig) {
        responses[response.statusCode] = response
    }


    fun getResponses() = responses.values.toList()

}
