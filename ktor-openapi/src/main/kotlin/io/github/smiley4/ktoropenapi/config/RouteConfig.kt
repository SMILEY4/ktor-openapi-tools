package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.ExternalDocsData
import io.github.smiley4.ktoropenapi.data.RouteData
import io.github.smiley4.ktoropenapi.data.ServerData

/**
 * Describes a single route including request and responses.
 */
@OpenApiDslMarker
class RouteConfig internal constructor() {

    /**
     * the id of the openapi-spec this route belongs to. 'Null' to use default spec.
     */
    var specName: String? = null


    /**
     * A list of tags for API documentation control. Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    var tags: Collection<String> = emptyList()

    /**
     * Set the list of tags for API documentation control.
     * Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    fun tags(tags: Collection<String>) {
        this.tags = tags
    }

    /**
     * Set the list of tags for API documentation control.
     * Tags can be used for logical grouping of operations by resources or any other qualifier
     */
    fun tags(vararg tags: String) {
        this.tags = tags.toList()
    }

    /**
     * A short summary of what the operation does.
     */
    var summary: String? = null


    /**
     * A verbose explanation of the operations' behavior.
     */
    var description: String? = null


    /**
     * Unique string used to identify the operation. The id MUST be unique among all operations described in the API.
     * The operationId value is case-sensitive.
     */
    var operationId: String? = null


    /**
     * Whether this route is deprecated
     */
    var deprecated: Boolean = false


    /**
     * Whether this route is hidden.
     */
    var hidden: Boolean = false


    /**
     * Specifies whether this operation is protected.
     * If not specified, the authentication state of the Ktor route will be used
     * (i.e. whether it is surrounded by an [authenticate][io.ktor.server.auth.authenticate] block or not).
     */
    var protected: Boolean? = null


    /**
     * A declaration of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    var securitySchemeNames: Collection<String>? = null

    /**
     * Set the declarations of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    fun securitySchemeNames(names: Collection<String>) {
        this.securitySchemeNames = names
    }

    /**
     * Set the declarations of which security mechanisms can be used for this operation (i.e. any of the specified ones).
     * If none is specified, defaultSecuritySchemeName (global plugin config) will be used.
     * Only applied to [protected] operations.
     */
    fun securitySchemeNames(vararg names: String) {
        this.securitySchemeNames = names.toList()
    }


    private val request = RequestConfig()


    /**
     * Information about the request
     */
    fun request(block: RequestConfig.() -> Unit) {
        request.apply(block)
    }

    fun getRequest() = request

    private val responses = ResponsesConfig()


    /**
     * Possible responses as they are returned from executing this operation.
     */
    fun response(block: ResponsesConfig.() -> Unit) {
        responses.apply(block)
    }

    fun getResponses() = responses


    /**
     * OpenAPI external docs configuration - link and description of an external documentation
     */
    fun externalDocs(block: ExternalDocsConfig.() -> Unit) {
        externalDocs = ExternalDocsConfig().apply(block)
    }

    private var externalDocs: ExternalDocsConfig? = null


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: ServerConfig.() -> Unit) {
        servers.add(ServerConfig().apply(block))
    }

    private val servers = mutableListOf<ServerConfig>()

    /**
     * Build the data object for this config.
     */
    internal fun build() = RouteData(
        specName = specName,
        tags = tags.toSet(),
        summary = summary,
        description = description,
        operationId = operationId,
        deprecated = deprecated,
        hidden = hidden,
        securitySchemeNames = securitySchemeNames?.toList() ?: emptyList(),
        protected = protected,
        request = request.build(),
        responses = responses.getResponses().map { it.build() },
        externalDocs = externalDocs?.build(ExternalDocsData.DEFAULT),
        servers = servers.map { it.build(ServerData.DEFAULT) }
    )

}
