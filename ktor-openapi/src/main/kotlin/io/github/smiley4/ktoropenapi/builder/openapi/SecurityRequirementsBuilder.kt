package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.swagger.v3.oas.models.security.SecurityRequirement

/**
 * Build the openapi [SecurityRequirement]-objects.
 * See [OpenAPI Specification - Security Requirement Object](https://swagger.io/specification/#security-requirement-object).
 */
internal class SecurityRequirementsBuilder(
    private val config: OpenApiPluginData
) {

    fun build(route: RouteMeta): List<SecurityRequirement> {
        return buildSet {
            addAll(route.documentation.securitySchemeNames)
            if(route.documentation.securitySchemeNames.isEmpty()) {
                addAll(config.securityConfig.defaultSecuritySchemeNames)
            }
        }.map {
            SecurityRequirement().apply {
                addList(it, emptyList())
            }
        }
    }

}
