package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.swagger.v3.oas.models.Operation

/**
 * Build the openapi [Operation]-object. Holds information describing a single API operation on a path.
 * See [OpenAPI Specification - Operation Object](https://swagger.io/specification/#operation-object).
 */
internal class OperationBuilder(
    private val operationTagsBuilder: OperationTagsBuilder,
    private val parameterBuilder: ParameterBuilder,
    private val requestBodyBuilder: RequestBodyBuilder,
    private val responsesBuilder: ResponsesBuilder,
    private val securityRequirementsBuilder: SecurityRequirementsBuilder,
    private val externalDocumentationBuilder: ExternalDocumentationBuilder,
    private val serverBuilder: ServerBuilder
) {

    fun build(route: RouteMeta): Operation =
        Operation().also {
            it.summary = route.documentation.summary
            it.description = route.documentation.description
            it.operationId = route.documentation.operationId
            it.deprecated = route.documentation.deprecated
            it.tags = operationTagsBuilder.build(route)
            it.parameters = route.documentation.request.parameters
                .filter { param -> !param.hidden }
                .map { param -> parameterBuilder.build(param) }
            route.documentation.request.body?.let { body ->
                it.requestBody = requestBodyBuilder.build(body)
            }
            it.responses = responsesBuilder.build(route.documentation.responses, route.protected)
            if (route.protected) {
                securityRequirementsBuilder.build(route).also { securityRequirements ->
                    if (securityRequirements.isNotEmpty()) {
                        it.security = securityRequirements
                    }
                }
            }
            it.externalDocs = route.documentation.externalDocs?.let { docs -> externalDocumentationBuilder.build(docs) }
            if (route.documentation.servers.isNotEmpty()) {
                it.servers = route.documentation.servers.map { server -> serverBuilder.build(server) }
            }
        }

}
