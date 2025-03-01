package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowData
import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowsData
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes

/**
 * Build the openapi [OAuthFlows]-object. Holds configuration of the supported OAuth Flows.
 * See [OpenAPI Specification - OAuth Flows Object](https://swagger.io/specification/#oauth-flows-object).
 */
internal class OAuthFlowsBuilder {

    fun build(flows: OpenIdOAuthFlowsData): OAuthFlows {
        return OAuthFlows().apply {
            implicit = flows.implicit?.let { build(it) }
            password = flows.password?.let { build(it) }
            clientCredentials = flows.clientCredentials?.let { build(it) }
            authorizationCode = flows.authorizationCode?.let { build(it) }
        }
    }

    private fun build(flow: OpenIdOAuthFlowData): OAuthFlow {
        return OAuthFlow().apply {
            authorizationUrl = flow.authorizationUrl
            tokenUrl = flow.tokenUrl
            refreshUrl = flow.refreshUrl
            scopes = flow.scopes?.let { buildScopes(it) }
        }
    }

    private fun buildScopes(scopes: Map<String, String>): Scopes {
        return Scopes().apply {
            scopes.forEach { (k, v) -> addString(k, v) }
        }
    }

}
