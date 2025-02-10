package io.github.smiley4.ktoropenapi.data

import io.github.smiley4.ktoropenapi.config.descriptors.ExampleDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.TypeDescriptor
import io.ktor.http.ContentType

/**
 * The common information for request and response bodies.
 */
internal sealed class BaseBodyData(
    val description: String?,
    val required: Boolean,
    val mediaTypes: Set<ContentType>,
)


/**
 * Information for a "simple" request or response body.
 */
internal class SimpleBodyData(
    description: String?,
    required: Boolean,
    mediaTypes: Set<ContentType>,
    val type: TypeDescriptor,
    val examples: List<ExampleDescriptor>
) : BaseBodyData(description, required, mediaTypes)


/**
 * Information for a multipart request or response body.
 */
internal class MultipartBodyData(
    description: String?,
    required: Boolean,
    mediaTypes: Set<ContentType>,
    val parts: List<MultipartPartData>
) : BaseBodyData(description, required, mediaTypes)
