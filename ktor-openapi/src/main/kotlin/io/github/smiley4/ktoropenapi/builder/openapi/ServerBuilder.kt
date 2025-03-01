package io.github.smiley4.ktoropenapi.builder.openapi

import io.github.smiley4.ktoropenapi.data.ServerData
import io.swagger.v3.oas.models.servers.Server
import io.swagger.v3.oas.models.servers.ServerVariable
import io.swagger.v3.oas.models.servers.ServerVariables

/**
 * Build the openapi [Server]-object. Holds information representing a Server.
 * See [OpenAPI Specification - Server Object](https://swagger.io/specification/#server-object).
 */
internal class ServerBuilder {

    fun build(server: ServerData): Server =
        Server().also {
            it.url = server.url
            it.description = server.description
            if (server.variables.isNotEmpty()) {
                it.variables = ServerVariables().also { variables ->
                    server.variables.forEach { entry ->
                        variables.addServerVariable(entry.name, ServerVariable().also { variable ->
                            variable.enum = entry.enum.toList()
                            variable.default = entry.default
                            variable.description = entry.description
                        })
                    }
                }
            }
        }

}
