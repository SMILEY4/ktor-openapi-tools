package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
    }

    install(OpenApi) {
        schemas {
            // configure the schema generator to use the default kotlinx-serializer
            generator = SchemaGenerator.kotlinx(json)
        }
        examples {
            // configure the example encoder to encode kotlin objects using kotlinx-serializer
            exampleEncoder = ExampleEncoder.kotlinx(json)
        }
    }

    routing {

        // add the routes for  the api-spec, swagger-ui and redoc
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
            description = "A Hello-World route"
            request {
                queryParameter<String>("name") {
                    description = "the name to greet"
                    example("Name Parameter") {
                        value = "Mr. Example"
                    }
                }
            }
            response {
                code(HttpStatusCode.OK) {
                    description = "successful request - always returns 'Hello World!'"
                    body<TestResponse> {
                        example("Success Response") {
                            value = TestResponse(
                                name = "Mr. Example",
                                length = 11
                            )
                        }
                    }
                }
            }
        }) {
            call.respondText("Hello ${call.request.queryParameters["name"]}")
        }

    }

}


@Serializable
data class TestResponse(
    val name: String,
    val length: Int,
)