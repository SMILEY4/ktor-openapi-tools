package io.github.smiley4.ktoropenapi.data

/**
 * See [OpenAPI Specification - OAuth Flows Object](https://swagger.io/specification/#oauth-flows-object).
 */
internal data class OpenIdOAuthFlowsData(
    val implicit: OpenIdOAuthFlowData?,
    val password: OpenIdOAuthFlowData?,
    val clientCredentials: OpenIdOAuthFlowData?,
    val authorizationCode: OpenIdOAuthFlowData?,
) {

    companion object {
        val DEFAULT = OpenIdOAuthFlowsData(
            implicit = null,
            password = null,
            clientCredentials = null,
            authorizationCode = null,
        )
    }

}
