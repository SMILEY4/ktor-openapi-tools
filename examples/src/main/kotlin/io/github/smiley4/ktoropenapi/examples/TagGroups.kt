package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.post
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

/**
 * Example demonstrating the x-tagGroups OpenAPI vendor extension.
 * Tag groups organize tags into logical sections in the documentation UI (e.g., Redoc).
 *
 * IMPORTANT: All tags used in your operations should be included in a tag group,
 * as tags not in any group may not be displayed in the documentation.
 *
 * Run the application and navigate to:
 * - Redoc: http://localhost:8080/redoc (recommended - has best x-tagGroups support)
 * - Swagger UI: http://localhost:8080/swagger
 * - OpenAPI Spec: http://localhost:8080/api.json
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::tagGroupsExample).start(wait = true)
}

private fun Application.tagGroupsExample() {

    // Install and configure the OpenApi Plugin with tag groups
    install(OpenApi) {
        info {
            title = "Tag Groups Example API"
            version = "1.0.0"
            description = "Example API demonstrating x-tagGroups for organizing tags in documentation"
        }

        // Configure tags with descriptions
        tags {
            tag("Users") {
                description = "User management operations"
            }
            tag("API Keys") {
                description = "API key management operations"
            }
            tag("Admin") {
                description = "Administrative operations"
            }
            tag("Orders") {
                description = "Order processing operations"
            }
            tag("Products") {
                description = "Product catalog operations"
            }
            tag("Analytics") {
                description = "Analytics and reporting"
            }

            // Define tag groups using the x-tagGroups extension
            // This organizes tags into logical groups in the documentation sidebar
            tagGroup("User Management") {
                tag("Users")
                tag("API Keys")
                tag("Admin")
            }

            tagGroup("E-Commerce") {
                tag("Orders")
                tag("Products")
            }

            tagGroup("Monitoring") {
                tag("Analytics")
            }
        }
    }

    routing {

        route("api.json") {
            openApi()
        }

        route("swagger") {
            swaggerUI("/api.json")
        }

        route("redoc") {
            redoc("/api.json")
        }

        // User Management endpoints
        get("users", {
            tags = listOf("Users")
            description = "List all users"
            response {
                HttpStatusCode.OK to {
                    description = "List of users"
                }
            }
        }) {
            call.respondText("[]")
        }

        post("users", {
            tags = listOf("Users")
            description = "Create a new user"
            response {
                HttpStatusCode.Created to {
                    description = "User created successfully"
                }
            }
        }) {
            call.respondText("User created", status = HttpStatusCode.Created)
        }

        get("api-keys", {
            tags = listOf("API Keys")
            description = "List API keys"
            response {
                HttpStatusCode.OK to {
                    description = "List of API keys"
                }
            }
        }) {
            call.respondText("[]")
        }

        post("admin/settings", {
            tags = listOf("Admin")
            description = "Update admin settings"
            response {
                HttpStatusCode.OK to {
                    description = "Settings updated"
                }
            }
        }) {
            call.respondText("Settings updated")
        }

        // E-Commerce endpoints
        get("orders", {
            tags = listOf("Orders")
            description = "List all orders"
            response {
                HttpStatusCode.OK to {
                    description = "List of orders"
                }
            }
        }) {
            call.respondText("[]")
        }

        get("products", {
            tags = listOf("Products")
            description = "List all products"
            response {
                HttpStatusCode.OK to {
                    description = "List of products"
                }
            }
        }) {
            call.respondText("[]")
        }

        // Monitoring endpoints
        get("analytics/stats", {
            tags = listOf("Analytics")
            description = "Get analytics statistics"
            response {
                HttpStatusCode.OK to {
                    description = "Analytics data"
                }
            }
        }) {
            call.respondText("{}")
        }

    }

}
