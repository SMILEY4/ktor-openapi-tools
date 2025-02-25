package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.OpenIdOAuthFlowsData
import io.github.smiley4.ktoropenapi.data.SecuritySchemeData


/**
 * Defines a security scheme that can be used by the operations. Supported schemes are HTTP authentication, an API key (either as a header,
 * a cookie parameter or as a query parameter), OAuth2's common flows (implicit, password, client credentials and authorization code)
 */
@OpenApiDslMarker
class SecuritySchemeConfig internal constructor(
    /**
     * The name of the security scheme.
     */
    private val schemeName: String
) {

    /**
     * The type of the security scheme
     */
    var type: AuthType? = null

    /**
     * The name scheme and of the header, query or cookie parameter to be used.
     */
    var name: String? = null

    /**
     * The location of the API key (OpenAPI 'in').
     * Required for type [AuthType.API_KEY]
     */
    var location: AuthKeyLocation? = null


    /**
     * The name of the HTTP Authorization scheme to be used.
     * Required for type [AuthType.HTTP]
     */
    var scheme: AuthScheme? = null


    /**
     * A hint to the client to identify how the bearer token is formatted.
     * Used for type [AuthType.HTTP] and schema [AuthScheme.BEARER]
     */
    var bearerFormat: String? = null

    private var flows: OpenIdOAuthFlowsConfig? = null


    /**
     * Information for the oauth flow types supported.
     * Required for type [AuthType.OAUTH2]
     */
    fun flows(block: OpenIdOAuthFlowsConfig.() -> Unit) {
        flows = OpenIdOAuthFlowsConfig().apply(block)
    }


    /**
     * OpenId Connect URL to discover OAuth2 configuration values.
     * Required for type [AuthType.OPENID_CONNECT]
     */
    var openIdConnectUrl: String? = null


    /**
     * A short description of the security scheme.
     */
    var description: String? = null

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: SecuritySchemeData) = SecuritySchemeData(
        schemeName = schemeName,
        type = merge(base.type, type),
        name = merge(base.name, name),
        location = merge(base.location, location),
        scheme = merge(base.scheme, scheme),
        bearerFormat = merge(base.bearerFormat, bearerFormat),
        flows = flows?.build(base.flows ?: OpenIdOAuthFlowsData.DEFAULT) ?: base.flows,
        openIdConnectUrl = merge(base.openIdConnectUrl, openIdConnectUrl),
        description = merge(base.description, description),
    )
}
