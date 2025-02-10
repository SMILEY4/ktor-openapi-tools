package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowData

/**
 * Configuration details for a supported OAuth Flow
 */
@OpenApiDslMarker
class OpenIdOAuthFlowConfig internal constructor() {

    /**
     * The authorization URL to be used for this flow
     */
    var authorizationUrl: String? = null

    /**
     * The token URL to be used for this flow
     */
    var tokenUrl: String? = null

    /**
     * The URL to be used for obtaining refresh tokens
     */
    var refreshUrl: String? = null

    /**
     * The available scopes for the OAuth2 security scheme. A map between the scope name and a short description for it
     */
    var scopes: Map<String, String>? = null

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: OpenIdOAuthFlowData) = OpenIdOAuthFlowData(
        authorizationUrl = merge(base.authorizationUrl, authorizationUrl),
        tokenUrl = merge(base.tokenUrl, tokenUrl),
        refreshUrl = merge(base.refreshUrl, refreshUrl),
        scopes = merge(base.scopes, scopes),
    )

}
