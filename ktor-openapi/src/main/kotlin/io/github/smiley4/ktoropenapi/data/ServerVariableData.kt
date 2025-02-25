package io.github.smiley4.ktoropenapi.data


/**
 * See [OpenAPI Specification - Server Variable Object](https://swagger.io/specification/#server-variable-object).
 */
internal data class ServerVariableData(
    val name: String,
    val enum: Set<String>,
    val default: String,
    val description: String?
)
