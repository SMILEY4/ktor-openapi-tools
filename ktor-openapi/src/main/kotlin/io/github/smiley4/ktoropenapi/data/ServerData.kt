package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - Server Object](https://swagger.io/specification/#server-object).
 */
internal data class ServerData(
    val url: String,
    val description: String?,
    val variables: List<ServerVariableData>
) {

    companion object {
        val DEFAULT = ServerData(
            url = "/",
            description = null,
            variables = emptyList()
        )
    }

}
