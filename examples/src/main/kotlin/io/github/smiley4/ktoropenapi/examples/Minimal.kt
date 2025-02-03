package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the Op lugin and use the default configuration
    install(OpenApi)

    routing {

        // Create a route for the OpenAPI spec file.
        // This route will not be included in the spec.
        route("api.json") {
            openApi()
        }
        // Create a route for the Swagger UI using the OpenAPI spec at "/api.json".
        // This route will not be included in the spec.
        route("swagger") {
            swaggerUI("/api.json")
        }
        // Create a route for ReDoc using the OpenAPI spec at "/api.json".
        // This route will not be included in the spec.
        route("redoc") {
            redoc("/api.json")
        }

        // a documented route
        get("hello", {
            // description of this route
            description = "A Hello-World route"
        }) {
            call.respondText("Hello World!")
        }

    }

}
