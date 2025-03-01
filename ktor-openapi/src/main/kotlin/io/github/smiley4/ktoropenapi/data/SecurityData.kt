package io.github.smiley4.ktoropenapi.data

/**
 * Common security configuration information.
 */
internal data class SecurityData(
    val defaultUnauthorizedResponse: ResponseData?,
    val defaultSecuritySchemeNames: Set<String>,
    val securitySchemes: List<SecuritySchemeData>,
) {
    companion object {
        val DEFAULT = SecurityData(
            defaultUnauthorizedResponse = null,
            defaultSecuritySchemeNames = emptySet(),
            securitySchemes = emptyList()
        )
    }
}
