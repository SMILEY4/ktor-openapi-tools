package io.github.smiley4.ktoropenapi.examples

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    // Install the OpenApi plugin and use the default configuration
    install(OpenApi)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
        }
    }

    routing {

        // add the routes for OpenAPI spec, Swagger UI and ReDoc
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
        post("calculate", {
            // information about the request
            request {
                // specify the schema of the request body and some additional information
                body<Calculation> {
                    description = "the requested operation and values to perform the operation on"
                    required = true
                }
            }
            // information the possible responses
            response {
                // document the "200 OK" response
                code(HttpStatusCode.OK) {
                    description = "Calculation was performed successfully."
                    // specify the schema of the response body and some additional information
                    body<CalculationResult> {
                        description = "the result of an operation together with the original request"
                    }
                }
                // document the "422 Unprocessable Entity" response
                code(HttpStatusCode.UnprocessableEntity) {
                    description = "The requested calculation could not be performed, e.g. due to division by zero."
                }
            }
        }) {
            call.receive<Calculation>().let { calculation ->
                when (calculation.operation) {
                    OperationType.ADD -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a + calculation.b
                            )
                        )
                    }
                    OperationType.SUB -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a - calculation.b
                            )
                        )
                    }
                    OperationType.MUL -> {
                        call.respond(
                            HttpStatusCode.OK, CalculationResult(
                                calculation = calculation,
                                result = calculation.a * calculation.b
                            )
                        )
                    }
                    OperationType.DIV -> {
                        if (calculation.b == 0f) {
                            call.respond(HttpStatusCode.UnprocessableEntity)
                        } else {
                            call.respond(
                                HttpStatusCode.OK, CalculationResult(
                                    calculation = calculation,
                                    result = calculation.a / calculation.b
                                )
                            )
                        }
                    }
                }
            }
        }

    }

}

private enum class OperationType {
    ADD, SUB, MUL, DIV
}

private data class Calculation(
    val operation: OperationType,
    val a: Float,
    val b: Float
)

private data class CalculationResult(
    val calculation: Calculation,
    val result: Float
)
