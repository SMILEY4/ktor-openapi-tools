package io.github.smiley4.ktorswaggerui

import io.github.smiley4.ktorswaggerui.config.SwaggerUIConfig
import io.github.smiley4.ktorswaggerui.config.OperationsSort
import io.github.smiley4.ktorswaggerui.config.SwaggerUISyntaxHighlight
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RoutingTests {

    @Test
    fun defaultConfig() = swaggerUITestApplication {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/swagger").also {
            it.status shouldBe HttpStatusCode.Found
            it.redirect shouldBe "/swagger/index.html"
        }
        get("/swagger/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body.shouldNotBeEmpty()
            it.body shouldContain "urls: [{name: 'Api', url: 'api.json'}]"
            it.body shouldContain "withCredentials: false"
            it.body shouldContain "displayOperationId: false"
            it.body shouldContain "filter: false"
            it.body shouldContain "syntaxHighlight: {theme: 'agate'}"
        }
    }

    @Test
    fun fullConfig() = swaggerUITestApplication({
        onlineSpecValidator()
        displayOperationId = true
        filter = true
        operationsSorter = OperationsSort.ALPHANUMERICALLY
        syntaxHighlight = SwaggerUISyntaxHighlight.MONOKAI
        withCredentials = true

    }) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/swagger").also {
            it.status shouldBe HttpStatusCode.Found
            it.redirect shouldBe "/swagger/index.html"
        }
        get("/swagger/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html
            it.body.shouldNotBeEmpty()
        }
        get("/swagger/swagger-initializer.js").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.JavaScript
            it.body.shouldNotBeEmpty()
            it.body shouldContain "urls: [{name: 'Api', url: 'api.json'}]"
            it.body shouldContain "withCredentials: true"
            it.body shouldContain "validatorUrl: 'https://validator.swagger.io/validator'"
            it.body shouldContain "displayOperationId: true"
            it.body shouldContain "filter: true"
            it.body shouldContain "operationsSorter: 'alpha'"
            it.body shouldContain "syntaxHighlight: {theme: 'monokai'}"
        }
    }

    private fun swaggerUITestApplication(config: SwaggerUIConfig.() -> Unit = {}, block: suspend TestContext.() -> Unit = {}) {
        testApplication {
            val client = createClient {
                this.followRedirects = false
            }
            routing {
                route("swagger") {
                    swaggerUI("api.json", config)
                }
                get("hello") {
                    call.respondText("Hello Test")
                }
            }
            TestContext(client).apply { block() }
        }
    }

    class TestContext(private val client: HttpClient) {

        suspend fun get(path: String): GetResult {
            return client.get(path)
                .let {
                    GetResult(
                        path = path,
                        status = it.status,
                        contentType = it.contentType(),
                        body = it.bodyAsText(),
                        redirect = it.headers["Location"]
                    )
                }
                .also { it.print() }
        }


        private fun GetResult.print() {
            println("GET ${this.path}  =>  ${this.status} (${this.contentType}): ${this.body}")
        }
    }

    data class GetResult(
        val path: String,
        val status: HttpStatusCode,
        val contentType: ContentType?,
        val body: String,
        val redirect: String?
    )

}
