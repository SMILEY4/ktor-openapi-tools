package io.github.smiley4.ktorswaggerui.dsl.routes

import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.OpenApiMultipartPartData
import io.github.smiley4.ktorswaggerui.data.SwaggerTypeDescriptor
import io.github.smiley4.ktorswaggerui.data.TypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.ktor.http.ContentType
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Describes one section of a multipart-body.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class OpenApiMultipartPart(
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
    val headers = mutableMapOf<String, OpenApiHeader>()


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: TypeDescriptor, block: OpenApiHeader.() -> Unit = {}) {
        headers[name] = OpenApiHeader().apply(block).apply {
            this.type = type
        }
    }


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: Schema<*>, block: OpenApiHeader.() -> Unit = {}) = header(name, SwaggerTypeDescriptor(type), block)


    /**
     * Possible headers for this part
     */
    fun header(name: String, type: KType, block: OpenApiHeader.() -> Unit = {}) = header(name, KTypeDescriptor(type), block)


    /**
     * Possible headers for this part
     */
    inline fun <reified T> header(name: String, noinline block: OpenApiHeader.() -> Unit = {}) =
        header(name, KTypeDescriptor(typeOf<T>()), block)

    /**
     * Build the data object for this config.
     */
    fun build() = OpenApiMultipartPartData(
        name = name,
        type = type,
        required = required,
        mediaTypes = mediaTypes.toSet(),
        headers = headers.mapValues { it.value.build() }
    )

}
