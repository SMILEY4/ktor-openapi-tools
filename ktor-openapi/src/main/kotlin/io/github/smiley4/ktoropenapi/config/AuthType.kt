package io.github.smiley4.ktoropenapi.config

import io.swagger.v3.oas.models.security.SecurityScheme

/**
 * The type of security schemes
 */
enum class AuthType(val swaggerType: SecurityScheme.Type) {
    API_KEY(SecurityScheme.Type.APIKEY),
    HTTP(SecurityScheme.Type.HTTP),
    OAUTH2(SecurityScheme.Type.OAUTH2),
    OPENID_CONNECT(SecurityScheme.Type.OPENIDCONNECT),
    MUTUAL_TLS(SecurityScheme.Type.MUTUALTLS)
}
