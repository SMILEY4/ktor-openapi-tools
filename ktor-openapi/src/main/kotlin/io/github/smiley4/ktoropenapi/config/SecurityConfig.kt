package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.SecurityData
import io.github.smiley4.ktoropenapi.data.SecuritySchemeData
import io.ktor.http.HttpStatusCode

/**
 * Configuration for security and authentication.
 */
@OpenApiDslMarker
class SecurityConfig internal constructor() {

    /**
     * Default response to automatically add to each protected route for the "Unauthorized"-Response-Code.
     * Generated response can be overwritten with custom response.
     */
    fun defaultUnauthorizedResponse(block: ResponseConfig.() -> Unit) {
        defaultUnauthorizedResponse = ResponseConfig(HttpStatusCode.Unauthorized.value.toString()).apply(block)
    }

    private var defaultUnauthorizedResponse: ResponseConfig? = null


    /**
     * The names of the security schemes available for use for the protected paths
     */
    var defaultSecuritySchemeNames: Collection<String>? = SecurityData.DEFAULT.defaultSecuritySchemeNames

    /**
     * Set the names of the security schemes available for use for the protected paths
     */
    fun defaultSecuritySchemeNames(names: Collection<String>) {
        this.defaultSecuritySchemeNames = names
    }

    /**
     * Set the names of the security schemes available for use for the protected paths
     */
    fun defaultSecuritySchemeNames(vararg names: String) {
        this.defaultSecuritySchemeNames = names.toList()
    }

    /**
     * Defines security schemes that can be used by operations
     */
    fun securityScheme(name: String, block: SecuritySchemeConfig.() -> Unit) {
        securitySchemes.add(SecuritySchemeConfig(name).apply(block))
    }

    private val securitySchemes = mutableListOf<SecuritySchemeConfig>()

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: SecurityData) = SecurityData(
        defaultUnauthorizedResponse = merge(base.defaultUnauthorizedResponse, defaultUnauthorizedResponse?.build()),
        defaultSecuritySchemeNames = buildSet {
            addAll(base.defaultSecuritySchemeNames)
            defaultSecuritySchemeNames?.also { addAll(it) }
        },
        securitySchemes = buildList {
            addAll(base.securitySchemes)
            addAll(securitySchemes.map { it.build(SecuritySchemeData.DEFAULT) })
        }
    )

}
