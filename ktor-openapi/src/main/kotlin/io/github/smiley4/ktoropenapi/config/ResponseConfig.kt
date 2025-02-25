package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.SwaggerTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.github.smiley4.ktoropenapi.data.ResponseData
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * A container for the expected responses of an operation. The container maps an HTTP response code to the expected response.
 * A response code can only have one response object.
 */
@OpenApiDslMarker
class ResponseConfig internal constructor(val statusCode: String) {

    /**
     * A short description of the response
     */
    var description: String? = null

    private val headers = mutableMapOf<String, HeaderConfig>()


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: TypeDescriptor, block: HeaderConfig.() -> Unit = {}) {
        headers[name] = HeaderConfig().apply(block).apply {
            this.type = type
        }
    }


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: Schema<*>, block: HeaderConfig.() -> Unit = {}) = header(name, SwaggerTypeDescriptor(type), block)


    /**
     * Possible headers returned with this response
     */
    fun header(name: String, type: KType, block: HeaderConfig.() -> Unit = {}) = header(name, KTypeDescriptor(type), block)


    /**
     * Possible headers returned with this response
     */
    inline fun <reified T> header(name: String, noinline block: HeaderConfig.() -> Unit = {}) =
        header(name, KTypeDescriptor(typeOf<T>()), block)


    private var body: BaseBodyConfig? = null


    /**
     * The body returned with this response
     */
    fun body(type: TypeDescriptor, block: SimpleBodyConfig.() -> Unit = {}) {
        body = SimpleBodyConfig(type).apply(block)
    }


    /**
     * The body returned with this response
     */
    fun body(type: Schema<*>, block: SimpleBodyConfig.() -> Unit = {}) = body(SwaggerTypeDescriptor(type), block)


    /**
     * The body returned with this response
     */
    fun body(type: KType, block: SimpleBodyConfig.() -> Unit = {}) = body(KTypeDescriptor(type), block)


    /**
     * The body returned with this response
     */
    inline fun <reified T> body(noinline block: SimpleBodyConfig.() -> Unit = {}) = body(KTypeDescriptor(typeOf<T>()), block)


    /**
     * The multipart-body returned with this response
     */
    fun multipartBody(block: MultipartBodyConfig.() -> Unit) {
        body = MultipartBodyConfig().apply(block)
    }


    /**
     * Don't include this response in the openapi-spec
     */
    var hidden: Boolean = false


    /**
     * Build the data object for this config.
     */
    internal fun build() = ResponseData(
        statusCode = statusCode,
        description = description,
        hidden = hidden,
        headers = headers.mapValues { it.value.build() },
        body = body?.build()
    )

}
