package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.MultipartPartData
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Describes one section of a multipart-body.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class MultipartPartConfig(
    /**
     * The name of this part
     */
    val name: String,

    val type: TypeDescriptor
) {

    /**
     * Whether this part is required
     */
    var required: Boolean = false

    /**
     * Specific content types for this part
     */
    var mediaTypes: Collection<ContentType> = setOf()

    /**
     * Set specific content types for this part
     */
    fun mediaTypes(types: Collection<ContentType>) {
        this.mediaTypes = types
    }

    /**
     * Set specific content types for this part
     */
    fun mediaTypes(vararg types: ContentType) {
        this.mediaTypes = types.toList()
    }

    /**
     * List of headers of this part
     */
    val headers = mutableMapOf<String, HeaderConfig>()


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: TypeDescriptor, block: HeaderConfig.() -> Unit = {}) {
        headers[name] = HeaderConfig().apply(block).apply {
            this.type = type
        }
    }


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: Schema<*>, block: HeaderConfig.() -> Unit = {}) = header(name, SwaggerTypeDescriptor(type), block)


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: KType, block: HeaderConfig.() -> Unit = {}) = header(name, KTypeDescriptor(type), block)


    /**
     * Possible headers for this part
     */
    inline fun <reified T> header(name: String, noinline block: HeaderConfig.() -> Unit = {}) =
        header(name, KTypeDescriptor(typeOf<T>()), block)

    /**
     * Build the data object for this config.
     */
    internal fun build() = MultipartPartData(
        name = name,
        type = type,
        required = required,
        mediaTypes = mediaTypes.toSet(),
        headers = headers.mapValues { it.value.build() }
    )

}
