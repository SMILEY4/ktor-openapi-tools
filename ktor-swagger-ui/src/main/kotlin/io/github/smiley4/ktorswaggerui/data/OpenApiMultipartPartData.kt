package io.github.smiley4.ktorswaggerui.data

import io.ktor.http.ContentType

/**
 * Information about a part of a multipart request or response body.
 */
data class OpenApiMultipartPartData(
    val name: String,
    val type: TypeDescriptor,
    val required: Boolean,
    val mediaTypes: Set<ContentType>,
    val headers: Map<String, OpenApiHeaderData>,
)
