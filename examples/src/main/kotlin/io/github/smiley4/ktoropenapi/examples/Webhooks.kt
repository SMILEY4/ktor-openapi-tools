package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.webhook
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the "OpenApi"-Plugin
    install(OpenApi)

    routing {

        // add the routes for the api-spec, swagger-ui and redoc
        route("api.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("redoc") {
            redoc("/api.json")
        }

        // a "normal" documented route to register for notifications
        post("registerForAlert", {
            description = "Register an URL to be called when new concerts are scheduled."
            request {
                body<String> {
                    description = "The URL to be notified about approaching concerts."
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        // documentation of the webhook to notify
        webhook(HttpMethod.Post, "concertAlert") {
            description = "Notify the registered URL with details of an upcoming concert"
            request {
                body<String> {
                    mediaTypes(ContentType.Text.Plain)
                    required = true
                }
            }
        }

    }

}
