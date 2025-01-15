package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.route
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
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

    // Install and configure the "SwaggerUI"-Plugin
    install(SwaggerUI) {
        // "global" configuration for all specs
        info {
            title = "Example API"
        }
        // configuration specific for spec "v1", overwrites global config
        spec("version1") {
            info {
                version = "1.0"
            }
        }
        // configuration specific for spec "v2", overwrites global config
        spec("version2") {
            info {
                version = "2.0"
            }
        }
        // assign all unassigned routes to spec "v2" (here only route '/greet')
        specAssigner = { _, _ -> "version2" }
    }

    routing {

        // add routes for "v1"-spec and swagger-ui
        route("v1") {
            route("swagger") {
                // swagger-ui using '/v1/api.json'
                swaggerUI("/v1/api.json")
            }
            route("api.json") {
                // api-spec containing all routes assigned to "v1"
                openApiSpec("version1")
            }
        }

        // add routes for "v2"-spec and swagger-ui
        route("v2") {
            route("swagger") {
                // swagger-ui using '/v2/api.json'
                swaggerUI("/v2/api.json")
            }
            route("api.json") {
                // api-spec containing all routes assigned to "v2"
                openApiSpec("version2")
            }
        }

        // version 1.0 routes
        route("v1", {
            specId = "version1"
        }) {

            // "hello"-route in version 1.0
            get("hello", {
                description = "Version 1 'Hello World'"
                request {
                    queryParameter<List<String>>("name")
                }
            }) {
                call.respondText("Hello World!")
            }

        }

        // version 2.0 routes
        route("v2", {
            specId = "version2"
        }) {

            // "hello"-route in version 2.0
            get("hello", {
                description = "Version 2 'Hello World'"
                request {
                    queryParameter<List<String>>("name")
                }
            }) {
                call.respondText("Hello World! (improved)")
            }

        }

        // unassigned route
        get("greet", {
            description = "Alternative route not manually assigned to any spec."
            request {
                queryParameter<List<String>>("name")
            }
        }) {
            call.respondText("Alternative Hello World!")
        }

    }

}