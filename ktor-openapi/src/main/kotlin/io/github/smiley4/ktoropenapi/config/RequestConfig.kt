package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.RequestData
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Describes a single request.
 */
@OpenApiDslMarker
class RequestConfig {

    /**
     * A list of parameters that are applicable for this operation
     */
    val parameters = mutableListOf<RequestParameterConfig>()


    /**
     * A path parameters that is applicable for this operation
     */
    fun parameter(location: ParameterLocation, name: String, type: TypeDescriptor, block: RequestParameterConfig.() -> Unit = {}) {
        parameters.add(RequestParameterConfig(name, type, location).apply(block))
    }


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: TypeDescriptor, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, type, block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: Schema<*>, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, SwaggerTypeDescriptor(type), block)


    /**
     * A path parameters that is applicable for this operation
     */
    fun pathParameter(name: String, type: KType, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, KTypeDescriptor(type), block)


    /**
     * A path parameters that is applicable for this operation
     */
    inline fun <reified T> pathParameter(name: String, noinline block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.PATH, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: TypeDescriptor, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, type, block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: Schema<*>, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, SwaggerTypeDescriptor(type), block)


    /**
     * A query parameters that is applicable for this operation
     */
    fun queryParameter(name: String, type: KType, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, KTypeDescriptor(type), block)


    /**
     * A query parameters that is applicable for this operation
     */
    inline fun <reified T> queryParameter(name: String, noinline block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.QUERY, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: TypeDescriptor, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, type, block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: Schema<*>, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, SwaggerTypeDescriptor(type), block)


    /**
     * A header parameters that is applicable for this operation
     */
    fun headerParameter(name: String, type: KType, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, KTypeDescriptor(type), block)


    /**
     * A header parameters that is applicable for this operation
     */
    inline fun <reified T> headerParameter(name: String, noinline block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.HEADER, name, KTypeDescriptor(typeOf<T>()), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: TypeDescriptor, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, type, block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: Schema<*>, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, SwaggerTypeDescriptor(type), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    fun cookieParameter(name: String, type: KType, block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, KTypeDescriptor(type), block)


    /**
     * A cookie parameters that is applicable for this operation
     */
    inline fun <reified T> cookieParameter(name: String, noinline block: RequestParameterConfig.() -> Unit = {}) =
        parameter(ParameterLocation.COOKIE, name, KTypeDescriptor(typeOf<T>()), block)


    private var body: BaseBodyConfig? = null

    fun getBody() = body


    /**
     * The body returned with this request
     */
    fun body(type: TypeDescriptor, block: SimpleBodyConfig.() -> Unit = {}) {
        val result = SimpleBodyConfig(type).apply(block)
        if (!result.isEmptyBody()) {
            body = result
        }
    }


    /**
     * The body returned with this request
     */
    fun body(type: Schema<*>, block: SimpleBodyConfig.() -> Unit = {}) = body(SwaggerTypeDescriptor(type), block)


    /**
     * The body returned with this request
     */
    fun body(type: KType, block: SimpleBodyConfig.() -> Unit = {}) = body(KTypeDescriptor(type), block)


    /**
     * The body returned with this request
     */
    inline fun <reified T> body(noinline block: SimpleBodyConfig.() -> Unit = {}) = body(KTypeDescriptor(typeOf<T>()), block)


    /**
     * The multipart-body returned with this request
     */
    fun multipartBody(block: MultipartBodyConfig.() -> Unit) {
        body = MultipartBodyConfig().apply(block)
    }


    /**
     * Set the body of this request.
     */
    internal fun setBody(body: BaseBodyConfig?) {
        this.body = body
    }


    /**
     * Build the data object for this config.
     */
    internal fun build() = RequestData(
        parameters = parameters.map { it.build() },
        body = body?.build()
    )

    private fun BaseBodyConfig.isEmptyBody(): Boolean {
        return when (this) {
            is SimpleBodyConfig -> when (type) {
                is KTypeDescriptor -> type.type == typeOf<Unit>()
                else -> false
            }
            is MultipartBodyConfig -> false
        }
    }

}
