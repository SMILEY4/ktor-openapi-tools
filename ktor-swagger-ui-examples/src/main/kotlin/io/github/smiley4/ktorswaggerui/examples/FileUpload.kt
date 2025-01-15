package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.array
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the "SwaggerUI"-Plugin and use the default configuration
    install(SwaggerUI) {
        schemas {
            // overwrite type "File" with custom schema for binary data
            overwrite<File>(Schema<Any>().also {
                it.types = setOf("string")
                it.format = "binary"
            })
        }
    }

    routing {

        // add the routes for swagger-ui and api-spec
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }

        // upload a single file, either as png, jpeg or svg
        post("single", {
            request {
                body<File> {
                    mediaTypes(
                        ContentType.Image.PNG,
                        ContentType.Image.JPEG,
                        ContentType.Image.SVG,
                    )
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

        // upload multiple (two) files
        post("multipart", {
            request {
                multipartBody {
                    mediaTypes(ContentType.MultiPart.FormData)
                    part<File>("first-image",) {
                        mediaTypes(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.SVG
                        )
                    }
                    part<File>("second-image") {
                        mediaTypes(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.SVG
                        )
                    }
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }

        // upload multiple (any amount of) files
        post("list", {
            request {
                multipartBody {
                    mediaTypes(ContentType.MultiPart.FormData)
                    part("images", array<File>()) {
                        mediaTypes(
                            ContentType.Image.PNG,
                            ContentType.Image.JPEG,
                            ContentType.Image.SVG
                        )
                    }
                }
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, "...")
        }
        
    }

}
