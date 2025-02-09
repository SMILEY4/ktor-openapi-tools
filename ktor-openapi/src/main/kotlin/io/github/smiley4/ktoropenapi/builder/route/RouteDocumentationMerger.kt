package io.github.smiley4.ktoropenapi.builder.route

import io.github.smiley4.ktoropenapi.config.RouteConfig

internal class RouteDocumentationMerger {

    /**
     * Merges "a" with "b" and returns the result as a new [RouteConfig]. "a" has priority over "b".
     */
    @Suppress("CyclomaticComplexMethod")
    fun merge(a: RouteConfig, b: RouteConfig): RouteConfig {
        return RouteConfig().apply {
            specName = a.specName ?: b.specName
            tags = mutableSetOf<String>().also {
                it.addAll(a.tags)
                it.addAll(b.tags)
            }
            summary = a.summary ?: b.summary
            description = a.description ?: b.description
            operationId = a.operationId ?: b.operationId
            securitySchemeNames = mutableSetOf<String>().also { merged ->
                a.securitySchemeNames?.let { merged.addAll(it) }
                b.securitySchemeNames?.let { merged.addAll(it) }
            }
            deprecated = a.deprecated || b.deprecated
            hidden = a.hidden || b.hidden
            protected = a.protected ?: b.protected
            request {
                buildMap {
                    b.getRequest().parameters.forEach { this[it.name] = it }
                    a.getRequest().parameters.forEach { this[it.name] = it }
                }.values.forEach { parameters.add(it) }
                setBody(a.getRequest().getBody() ?: b.getRequest().getBody())
            }
            response {
                buildMap {
                    b.getResponses().getResponses().forEach { this[it.statusCode] = it }
                    a.getResponses().getResponses().forEach { this[it.statusCode] = it }
                }.values.forEach { addResponse(it) }
            }
        }
    }

}
