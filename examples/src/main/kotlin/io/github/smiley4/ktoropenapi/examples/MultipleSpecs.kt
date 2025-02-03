package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.route
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

    // Install and configure the OpenAPI plugin
    install(OpenApi) {
        // "global" configuration for all specs
        info {
            title = "Example API"
        }
        // configuration specific for spec "version1", overwrites global config
        spec("version1") {
            info {
                version = "1.0"
            }
        }
        // configuration specific for spec "version2", overwrites global config
        spec("version2") {
            info {
                version = "2.0"
            }
        }
        // assign all unassigned routes to spec "version2" (here only route '/greet')
        specAssigner = { _, _ -> "version2" }
    }

    routing {

        // add routes for "version1" spec, Swagger UI and ReDoc
        route("v1") {
            // OpenAPI spec containing all routes assigned to "version1"
            route("api.json") {
                openApi("version1")
            }
            // Swagger UI using '/v1/api.json'
            route("swagger") {
                swaggerUI("/v1/api.json")
            }
            // ReDoc using '/v1/api.json'
            route("redoc") {
                redoc("/v1/api.json")
            }
        }

        // add routes for "version2" spec, Swagger UI and ReDoc
        route("v2") {
            // OpenAPI spec containing all routes assigned to "version2"
            route("api.json") {
                openApi("version2")
            }
            // Swagger UI using '/v2/api.json'
            route("swagger") {
                swaggerUI("/v2/api.json")
            }
            // ReDoc using '/v2/api.json'
            route("swagger") {
                redoc("/v2/api.json")
            }
        }

        // version 1.0 routes
        route("v1", {
            specName = "version1"
        }) {

            // "hello" route in version 1.0
            get("hello", {
                description = "Version 1 'Hello World'"
            }) {
                call.respondText("Hello World!")
            }

        }

        // version 2.0 routes
        route("v2", {
            specName = "version2"
        }) {

            // "hello" route in version 2.0
            get("hello", {
                description = "Version 2 'Hello World'"
            }) {
                call.respondText("Hello World! (improved)")
            }

        }

        // unassigned route
        get("greet", {
            description = "Alternative route not manually assigned to any spec."
        }) {
            call.respondText("Alternative Hello World!")
        }

    }

}
