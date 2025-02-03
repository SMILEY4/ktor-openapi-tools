package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install "Authentication" Plugin and setup e.g. Basic-Auth
    // username = "user",  password = "pass"
    install(Authentication) {
        basic {
            realm = "Access to the API"
            validate { credentials ->
                if (credentials.name == "user" && credentials.password == "pass") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }

    // Install and configure the OpenAPI plugin
    install(OpenApi) {
        schemas {  }
        security {
            // configure a basic-auth security scheme
            securityScheme("MySecurityScheme") {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            }
            // if no other security scheme is specified for a route, the one with this name is used instead
            defaultSecuritySchemeNames("MySecurityScheme")
            // if no other response is documented for "401 Unauthorized", this information is used instead
            defaultUnauthorizedResponse {
                description = "Username or password is invalid"
                body<AuthRequired>()
            }
        }
    }

    routing {

        // add the routes for  the api spec, swagger-ui and redoc
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApi()
        }
        route("redoc") {
            redoc("/api.json")
        }

        authenticate {
            // route is in an "authenticate" block -> default security scheme will be used (if not specified otherwise)
            get("protected", {
                // response for "401 Unauthorized" is automatically added if configured in the plugin config and not specified otherwise
            }) {
                call.respondText("Hello World!")
            }

            get("protected2", {
                // response for "401 Unauthorized" is automatically added if configured in the plugin config and not specified otherwise
            }) {
                call.respondText("Hello World!")
            }
        }

        // route is not in an "authenticate" block but "protected" flag is set (e.g. because is it protected by an external reverse-proxy
        // -> specified or default security scheme is used and default "401 Unauthorized" is added if not specified otherwise
        get("externally-protected", {
            // manually specify that this route requires authentication
            protected = true
        }) {
            call.respondText("Hello World!")
        }

        // route is not in an "authenticate" block and "protected" flag is not set
        // -> security schemes will be ignored and not default "401 Unauthorized" response is added
        get("unprotected", {
            securitySchemeNames("MySecurityScheme")
        }) {
            call.respondText("Hello World!")
        }

    }

}


class AuthRequired(val message: String)
