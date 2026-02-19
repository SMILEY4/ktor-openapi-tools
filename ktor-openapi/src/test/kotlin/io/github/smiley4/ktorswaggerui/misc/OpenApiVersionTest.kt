package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OpenApiVersion
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldStartWith
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlin.test.Test

class OpenApiVersionTest {

    /** A data class with a nullable field — triggers the "null" type in schema-kenerator output. */
    data class NullableBody(val text: String?)

    @Test
    fun `default version is 3_1_0`() = versionTestApplication(OpenApiVersion.V3_1) { client ->
        client.get("/api.json").also { response ->
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldStartWith "{\n  \"openapi\" : \"3.1.0\","
        }
    }

    @Test
    fun `v3_0 json produces version string 3_0_3`() = versionTestApplication(OpenApiVersion.V3_0) { client ->
        client.get("/api.json").also { response ->
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldStartWith "{\n  \"openapi\" : \"3.0.3\","
        }
    }

    @Test
    fun `v3_0 yaml produces version string 3_0_3`() = versionTestApplication(OpenApiVersion.V3_0, OutputFormat.YAML) { client ->
        client.get("/api.yaml").also { response ->
            response.status shouldBe HttpStatusCode.OK
            response.bodyAsText() shouldStartWith "openapi: 3.0.3\n"
        }
    }

    @Test
    fun `v3_0 transforms nullable property types to nullable boolean`() = versionTestApplication(OpenApiVersion.V3_0) { client ->
        val body = client.get("/api.json").bodyAsText()
        // NullableBody.text is String? — after 3.0 transformation: type:"string" + nullable:true, no "null" in type arrays
        body shouldContain "\"nullable\" : true"
        body shouldNotContain "\"null\""
    }

    @Test
    fun `v3_1 keeps nullable property types as type array`() = versionTestApplication(OpenApiVersion.V3_1) { client ->
        val body = client.get("/api.json").bodyAsText()
        // NullableBody.text is String? — in 3.1 format: types array includes "null"
        body shouldContain "\"null\""
        body shouldNotContain "\"nullable\" : true"
    }

    private fun versionTestApplication(
        version: OpenApiVersion,
        format: OutputFormat = OutputFormat.JSON,
        block: suspend (HttpClient) -> Unit,
    ) {
        testApplication {
            val client = createClient { followRedirects = false }
            install(OpenApi) {
                outputFormat = format
                openApiVersion = version
            }
            routing {
                val suffix = if (format == OutputFormat.YAML) "yaml" else "json"
                route("api.$suffix") {
                    openApi()
                }
                // Route with a body type that has a nullable field, to exercise nullable schema handling
                get("hello", {
                    response {
                        HttpStatusCode.OK to {
                            body<NullableBody>()
                        }
                    }
                }) {
                    call.respondText("Hello")
                }
            }
            block(client)
        }
    }
}
