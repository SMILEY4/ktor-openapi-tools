package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    routing {

        // Create a route for the swagger ui using an external OpenAPI spec.
        route("swagger") {
            swaggerUI("https://petstore3.swagger.io/api/v3/openapi.json")
        }

        // Create a route for Redoc using an external OpenAPI spec.
        route("redoc") {
            redoc("https://petstore3.swagger.io/api/v3/openapi.json")
        }

    }

}
