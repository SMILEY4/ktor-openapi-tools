package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.SwaggerTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.github.smiley4.ktoropenapi.data.MultipartBodyData
import io.swagger.v3.oas.models.media.Schema
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/**
 * Describes a single request/response body with multipart content.
 * See https://swagger.io/docs/specification/describing-request-body/multipart-requests/ for more info
 */
@OpenApiDslMarker
class MultipartBodyConfig : BaseBodyConfig() {

    private val parts = mutableListOf<MultipartPartConfig>()


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: TypeDescriptor, block: MultipartPartConfig.() -> Unit = {}) {
        parts.add(MultipartPartConfig(name, type).apply(block))
    }


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: Schema<*>, block: MultipartPartConfig.() -> Unit = {}) = part(name, SwaggerTypeDescriptor(type), block)


    /**
     * One part of a multipart-body
     */
    fun part(name: String, type: KType, block: MultipartPartConfig.() -> Unit = {}) = part(name, KTypeDescriptor(type), block)


    /**
     * One part of a multipart-body
     */
    inline fun <reified T> part(name: String, noinline block: MultipartPartConfig.() -> Unit = {}) =
        part(name, KTypeDescriptor(typeOf<T>()), block)


    override fun build() = MultipartBodyData(
        description = description,
        required = required ?: false,
        mediaTypes = mediaTypes.toSet(),
        parts = parts.map { it.build() }
    )
}
