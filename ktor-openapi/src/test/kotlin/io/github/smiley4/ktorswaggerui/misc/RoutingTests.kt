package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.openApi
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.request.get
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
    fun jsonSpec() = openApiTestApplication {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/api.json").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Application.Json
            it.body.shouldNotBeEmpty()
            it.body shouldStartWith "{\n  \"openapi\" : \"3.1.0\","
        }
    }


    @Test
    fun yamlSpec() = openApiTestApplication(OutputFormat.YAML) {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/").also {
            it.status shouldBe HttpStatusCode.NotFound
        }
        get("/api.yml").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Plain.withParameter("charset", "utf-8")
            it.body.shouldNotBeEmpty()
            it.body shouldStartWith "openapi: 3.1.0\n"
        }
    }

    private fun openApiTestApplication(format: OutputFormat = OutputFormat.JSON, block: suspend TestContext.() -> Unit) {
        testApplication {
            val client = createClient {
                this.followRedirects = false
            }
            install(OpenApi) {
                outputFormat = format
            }
            routing {
                val routeSuffix = when(format) {
                    OutputFormat.JSON -> "json"
                    OutputFormat.YAML -> "yml"
                }
                route("api.$routeSuffix") {
                    openApi()
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
