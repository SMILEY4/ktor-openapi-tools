package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.TypeDescriptor
import io.ktor.http.ContentType

/**
 * Information about a part of a multipart request or response body.
 */
internal data class MultipartPartData(
    val name: String,
    val type: TypeDescriptor,
    val required: Boolean,
    val mediaTypes: Set<ContentType>,
    val headers: Map<String, HeaderData>,
)
