package io.github.smiley4.ktoropenapi.data

/**
 * Information about a request
 */
internal data class RequestData(
    val parameters: List<RequestParameterData>,
    val body: BaseBodyData?,
)
