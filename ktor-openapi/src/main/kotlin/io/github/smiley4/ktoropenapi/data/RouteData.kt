package io.github.smiley4.ktoropenapi.data

/**
 * Information about a single route.
 */
internal data class RouteData(
    val specName: String?,
    val tags: Set<String>,
    val summary: String?,
    val description: String?,
    val operationId: String?,
    val deprecated: Boolean,
    val hidden: Boolean,
    val securitySchemeNames: List<String>,
    val protected: Boolean?,
    val request: RequestData,
    val responses: List<ResponseData>,
    val externalDocs: ExternalDocsData?,
    val servers: List<ServerData>,
)
