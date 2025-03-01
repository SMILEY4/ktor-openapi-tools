package io.github.smiley4.ktoropenapi.data

/**
 * Information about a response for a status-code.
 */
internal data class ResponseData(
    val statusCode: String,
    val description: String?,
    val hidden: Boolean,
    val headers: Map<String, HeaderData>,
    val body: BaseBodyData?,
)
