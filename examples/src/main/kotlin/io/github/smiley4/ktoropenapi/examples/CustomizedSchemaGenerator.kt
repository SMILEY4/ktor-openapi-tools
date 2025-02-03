package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.serialization.analyzeTypeUsingKotlinxSerialization
import io.github.smiley4.schemakenerator.swagger.RequiredHandling
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.withTitle
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install and configure the OpenApi plugin
    install(OpenApi) {
        schemas {
            // replace default schema-generator with configurable pre-defined generator, or ...
            generator = SchemaGenerator.kotlinx {
                nullables = RequiredHandling.NON_REQUIRED
                optionals = RequiredHandling.REQUIRED
                title = TitleType.SIMPLE
                explicitNullTypes = false
            }
            // ... replace default schema-generator with completely custom generator
            generator = { type ->
                type
                    .analyzeTypeUsingKotlinxSerialization()
                    .generateSwaggerSchema {
                        nullables = RequiredHandling.NON_REQUIRED
                        optionals = RequiredHandling.REQUIRED
                    }
                    .withTitle(TitleType.SIMPLE)
                    .compileReferencingRoot(
                        explicitNullTypes = false
                    )
            }
        }
    }

    routing {

        // add the routes for  the OpenAPI spec, Swagger UI and ReDoc
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApi()
        }
        route("redoc") {
            redoc("/api.json")
        }

        // a documented route
        get("hello", {
            // information about the request
            response {
                // information about a "200 OK" response
                code(HttpStatusCode.OK) {
                    // body of the response
                    body<MyResponseBody>()
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

    }

}

@Serializable
private class MyResponseBody(
    val name: String,
)
