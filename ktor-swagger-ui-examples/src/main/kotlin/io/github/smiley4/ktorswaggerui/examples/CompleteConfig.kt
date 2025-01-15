package io.github.smiley4.ktorswaggerui.examples

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSort
import io.github.smiley4.ktorswaggerui.data.SwaggerUiSyntaxHighlight
import io.github.smiley4.ktorswaggerui.dsl.config.PluginConfigDsl
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.github.smiley4.schemakenerator.reflection.processReflection
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.withAutoTitle
import io.github.smiley4.schemakenerator.swagger.withTitle
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.swagger.v3.oas.models.media.Schema
import java.io.File

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

class Greeting(
    val name: String
)

/**
 * A (nearly) complete - and mostly nonsensical - plugin configuration
 */
private fun Application.myModule() {


    install(SwaggerUI) {
        info {
            title = "Example API"
            version = "latest"
            description = "An example api."
            termsOfService = "example.com"
            contact {
                name = "Mr. Example"
                url = "example.com"
                email = "example@example.com"
            }
            license {
                name = "Example License"
                url = "example.com"
                identifier = "Apache-2.0"
            }
        }
        externalDocs {
            url = "example.com"
            description = "Project documentation"
        }
        server {
            url = "localhost"
            description = "local dev-server"
            variable("version") {
                default = "1.0"
                enum = setOf("1.0", "2.0", "3.0")
                description = "the version of the server api"
            }
        }
        server {
            url = "example.com"
            description = "productive server"
            variable("version") {
                default = "1.0"
                enum = setOf("1.0", "2.0")
                description = "the version of the server api"
            }
        }
        swagger {
            displayOperationId = true
            showTagFilterInput = true
            sort = SwaggerUiSort.HTTP_METHOD
            syntaxHighlight = SwaggerUiSyntaxHighlight.MONOKAI
            withCredentials = false
        }
        security {
            defaultUnauthorizedResponse {
                description = "Username or password is invalid"
            }
            defaultSecuritySchemeNames("MySecurityScheme")
            securityScheme("MySecurityScheme") {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
        }
        tags {
            tagGenerator = { url -> listOf(url.firstOrNull()) }
            tag("users") {
                description = "routes to manage users"
                externalDocUrl = "example.com"
                externalDocDescription = "Users documentation"
            }
            tag("documents") {
                description = "routes to manage documents"
                externalDocUrl = "example.com"
                externalDocDescription = "Document documentation"
            }
        }
        schemas {
            schema<String>("string")
            generator = { type ->
                type
                    .processReflection()
                    .generateSwaggerSchema()
                    .withTitle(TitleType.SIMPLE)
                    .compileReferencingRoot()
            }
            overwrite<File>(Schema<Any>().also {
                it.types = setOf("string")
                it.format = "binary"
            })
        }
        examples {
            example("Id 1") {
                description = "First example id"
                value = "12345"
            }
            example("Id 2") {
                description = "Second example id"
                value = "54321"

            }
        }
        specAssigner = { _, _ -> PluginConfigDsl.DEFAULT_SPEC_ID }
        pathFilter = { _, url -> url.firstOrNull() != "hidden" }
        ignoredRouteSelectors = emptySet()
        ignoredRouteSelectorClassNames = emptySet()
        postBuild = { api, name -> println("Completed api '$name': $api") }
    }


    routing {

        // add the routes for swagger-ui and api-spec
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }

        // a documented route
        get("hello", {
            operationId = "hello"
            summary = "hello world route"
            description = "A Hello-World route as an example."
            tags("hello", "example")
            specId = PluginConfigDsl.DEFAULT_SPEC_ID
            deprecated = false
            hidden = false
            protected = false
            securitySchemeNames(emptyList())
            externalDocs {
                url = "example.com/hello"
                description = "external documentation of 'hello'-route"
            }
            request {
                queryParameter<String>("name") {
                    description = "the name to greet"
                    example("Josh") {
                        value = "Josh"
                        summary = "Example name"
                        description = "An example name for this query parameter"
                    }
                }
                body<Unit>()
            }
            response {
                code(HttpStatusCode.OK) {
                    description = "successful request - always returns 'Hello World!'"
                    header<String>("x-random") {
                        description = "A header with some random number"
                        required = true
                        deprecated = false
                        explode = false
                    }
                    body<Greeting> {
                        description = "the greeting object with the name of the person to greet."
                        mediaTypes(ContentType.Application.Json)
                        required = true
                    }
                }
            }
            server {
                url = "example.com"
                description = "productive server for 'hello'-route"
                variable("version") {
                    default = "1.0"
                    enum = setOf("1.0", "2.0")
                    description = "the version of the server api"
                }
            }
        }) {
            call.respondText("Hello ${call.request.queryParameters["name"]}")
        }

    }

}
