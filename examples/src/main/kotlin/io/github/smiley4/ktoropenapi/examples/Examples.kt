package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.KTypeDescriptor
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
import kotlin.reflect.typeOf

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install and customize the OpenApi plugin
    install(OpenApi) {
        examples {

            // specify two shared examples
            example("Shared A") {
                description = "first shared example"
                value = MyExampleClass(
                    someValue = "shared a"
                )
            }
            example("Shared B") {
                description = "second shared example"
                value = MyExampleClass(
                    someValue = "shared b"
                )
            }

            // encoder used in "custom-encoder" example
            encoder { type, example ->
                // encode just the wrapped value for CustomEncoderData class
                if (type is KTypeDescriptor && type.type == typeOf<CustomEncoderData>())
                    (example as CustomEncoderData).number
                // return the example unmodified to fall back to default encoder
                else
                    example
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


        get("basic", {
            request {
                body<MyExampleClass> {
                    // specify two example values
                    example("Example 1") {
                            description = "A first example value"
                            value = MyExampleClass(
                                someValue = "example 1"
                            )
                    }
                    example("Example 2") {
                        description = "A second example value"
                        value = MyExampleClass(
                            someValue = "example 2"
                        )
                    }
                }
            }
        }) {
            call.respondText("...")
        }


        get("reference-shared", {
            request {
                body<MyExampleClass> {
                    // reference two shared examples specified in the plugin config (and placed in the component section)
                    exampleRef("Example 1", "Shared A")
                    exampleRef("Example 2", "Shared B")
                }
            }
        }) {
            call.respondText("...")
        }

        get("custom-encoder", {
            request {
                body<CustomEncoderData> {
                    // The type is CustomEncoderData, but it's actually encoded as a plain number
                    // See configuration for encoder
                    example("Example 1") {
                        value = CustomEncoderData(123)
                    }
                }
            }
        }) {
            call.respondText("...")
        }
    }

}


private data class MyExampleClass(
    val someValue: String
)

private data class CustomEncoderData(
    val number: Int
)
