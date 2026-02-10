package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.config.descriptors.anyOf
import io.github.smiley4.ktoropenapi.config.descriptors.array
import io.github.smiley4.ktoropenapi.config.descriptors.ref
import io.github.smiley4.ktoropenapi.config.descriptors.type
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.reflection.analyzer.TypeCategoryAnalyzer.Companion.DEFAULT_PRIMITIVE_TYPES
import io.github.smiley4.schemakenerator.reflection.data.EnumConstType
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.examples.Example
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install and configure the "OpenApi" Plugin
    install(OpenApi) {

        outputFormat = OutputFormat.JSON

        // configure basic information about the api
        info {
            title = "Example API"
            description = "An example api to showcase basic swagger-ui functionality."
        }
        // provide a reference to an external documentation
        externalDocs {
            url = "https://github.com/SMILEY4/ktor-swagger-ui/wiki"
            description = "Sample external documentation"
        }
        // configure the servers from where the api is being served
        server {
            url = "http://localhost:8080"
            description = "Development Server"
        }
        server {
            url = "https://www.example.com"
            description = "Production Server"
        }
        examples {
            encoder(ExampleEncoder.jackson())
        }
    }

    routing {

        // Create a route for the openapi spec file.
        // This route will not be included in the spec.
        route("api.json") {
            openApi()
        }
        // Create a route for the Swagger UI using the openapi spec at "/api.json".
        // This route will not be included in the spec.
        route("swagger") {
            swaggerUI("/api.json")
        }
        // Create a route for redoc using the OpenAPI spec at "/api.json".
        // This route will not be included in the spec.
        route("redoc") {
            redoc("/api.json")
        }

        // a documented route
        get("hello", {
            // description of the route
            description = "A Hello-World route"
            // information about the request
            request {
                // information about the query parameter "name" of type "string"
                queryParameter<String>("name") {
                    description = "the name to greet"
                }
                body<String>() {
                    exampleRef("Standard User", "standard-user")
                }
            }
            // information about possible responses
            response {
                // information about a "200 OK" response
                code(HttpStatusCode.OK) {
                    // a description of the response
                    description = "successful request - always returns 'Hello World!'"
                }
            }
        }) {
            call.respondText("Hello ${call.request.queryParameters["name"]}")
        }

    }

}
