package io.github.smiley4.ktorredoc

import io.github.smiley4.ktorredoc.config.RedocConfig
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class RoutingTests {

    @Test
    fun defaultConfig() = redocTestApplication {
        get("hello").also {
            it.status shouldBe HttpStatusCode.OK
            it.body shouldBe "Hello Test"
        }
        get("/redoc").also {
            it.status shouldBe HttpStatusCode.Found
            it.redirect shouldBe "/redoc/index.html"
        }
        get("/redoc/index.html").also {
            it.status shouldBe HttpStatusCode.OK
            it.contentType shouldBe ContentType.Text.Html.withCharset(java.nio.charset.Charset.forName("UTF-8"))
            it.body.shouldNotBeEmpty()
        }
    }

    private fun redocTestApplication(config: RedocConfig.() -> Unit = {}, block: suspend TestContext.() -> Unit = {}) {
        testApplication {
            val client = createClient {
                this.followRedirects = false
            }
            routing {
                route("redoc") {
                    redoc("api.json", config)
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
