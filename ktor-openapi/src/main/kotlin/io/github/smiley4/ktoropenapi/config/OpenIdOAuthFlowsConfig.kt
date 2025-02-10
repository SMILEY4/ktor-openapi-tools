package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowData
import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowsData

/**
 * An object containing configuration information for the oauth flow types supported
 */
@OpenApiDslMarker
class OpenIdOAuthFlowsConfig internal constructor() {

    private var implicit: OpenIdOAuthFlowConfig? = null


    /**
     * Configuration for the OAuth Implicit flow
     */
    fun implicit(block: OpenIdOAuthFlowConfig.() -> Unit) {
        implicit = OpenIdOAuthFlowConfig().apply(block)
    }


    private var password: OpenIdOAuthFlowConfig? = null


    /**
     * Configuration for the OAuth Resource Owner Password flow
     */
    fun password(block: OpenIdOAuthFlowConfig.() -> Unit) {
        password = OpenIdOAuthFlowConfig().apply(block)
    }


    private var clientCredentials: OpenIdOAuthFlowConfig? = null


    /**
     * Configuration for the OAuth Client Credentials flow.
     */
    fun clientCredentials(block: OpenIdOAuthFlowConfig.() -> Unit) {
        clientCredentials = OpenIdOAuthFlowConfig().apply(block)
    }


    private var authorizationCode: OpenIdOAuthFlowConfig? = null


    /**
     * Configuration for the OAuth Authorization Code flow.
     */
    fun authorizationCode(block: OpenIdOAuthFlowConfig.() -> Unit) {
        authorizationCode = OpenIdOAuthFlowConfig().apply(block)
    }

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: OpenIdOAuthFlowsData) = OpenIdOAuthFlowsData(
        implicit = implicit?.build(base.implicit ?: OpenIdOAuthFlowData.DEFAULT) ?: base.implicit,
        password = password?.build(base.password ?: OpenIdOAuthFlowData.DEFAULT) ?: base.password,
        clientCredentials = clientCredentials?.build(base.clientCredentials ?: OpenIdOAuthFlowData.DEFAULT) ?: base.clientCredentials,
        authorizationCode = authorizationCode?.build(base.authorizationCode ?: OpenIdOAuthFlowData.DEFAULT) ?: base.authorizationCode,
    )

}
