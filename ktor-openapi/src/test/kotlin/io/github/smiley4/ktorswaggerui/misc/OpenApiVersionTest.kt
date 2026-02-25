package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.OpenApiPluginConfig
import io.github.smiley4.ktoropenapi.config.OpenApiVersion
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.schemakenerator.core.annotations.ExclusiveMax
import io.github.smiley4.schemakenerator.core.annotations.ExclusiveMin
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
import io.swagger.v3.oas.models.media.Schema
import kotlin.test.Test

class OpenApiVersionTest {

    /** A data class with a nullable field — triggers the "null" type in schema-kenerator output. */
    data class NullableBody(val text: String?)


    /** A data class with exclusive min/max constraints — triggers exclusiveMinimumValue/exclusiveMaximumValue. */
    data class BoundedBody(@ExclusiveMin(1) @ExclusiveMax(100) val count: Int)


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

    // --- exclusiveMinimum / exclusiveMaximum ---

    @Test
    fun `v3_0 transforms exclusiveMinimumValue to boolean flag alongside minimum`() =
        versionTestApplication(OpenApiVersion.V3_0) { client ->
            val body = client.get("/api.json").bodyAsText()
            // BoundedBody.count has @ExclusiveMin(1) @ExclusiveMax(100)
            // After 3.0 transform: exclusiveMinimum/Maximum are booleans, not numbers
            body shouldContain "\"exclusiveMinimum\" : true"
            body shouldContain "\"exclusiveMaximum\" : true"
        }


    @Test
    fun `v3_1 keeps exclusiveMinimum as numeric value`() = versionTestApplication(OpenApiVersion.V3_1) { client ->
        val body = client.get("/api.json").bodyAsText()
        // In OAS 3.1, exclusiveMinimum is a number — boolean "true" must not appear
        body shouldNotContain "\"exclusiveMinimum\" : true"
        body shouldNotContain "\"exclusiveMaximum\" : true"
    }

    // --- schema-level examples list ---

    @Test
    fun `v3_0 transforms schema examples list to single example`() = versionTestApplication(
        version = OpenApiVersion.V3_0,
        openApiConfig = {
            postBuild = { openApi, _ ->
                // Inject a 3.1-style examples list into all component schemas before the transform runs
                @Suppress("UNCHECKED_CAST")
                openApi.components?.schemas?.values?.forEach { schema ->
                    (schema as Schema<Any>).examples = listOf("example-value")
                }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        // After transform: first element promoted to singular "example", list cleared.
        // Note: "examples" : { } from components.examples (shared map) is distinct from schema-level "examples" : [...]
        body shouldContain "\"example\" : \"example-value\""
        body shouldNotContain "\"examples\" : ["
    }


    @Test
    fun `v3_1 keeps schema examples list unchanged`() = versionTestApplication(
        version = OpenApiVersion.V3_1,
        openApiConfig = {
            postBuild = { openApi, _ ->
                @Suppress("UNCHECKED_CAST")
                openApi.components?.schemas?.values?.forEach { schema ->
                    (schema as Schema<Any>).examples = listOf("example-value")
                }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        // No transform in 3.1 — list is preserved as an array
        body shouldContain "\"examples\" : ["
        body shouldContain "\"example-value\""
    }

    // --- contentEncoding / contentMediaType ---

    @Test
    fun `v3_0 transforms contentEncoding base64 to format byte`() = versionTestApplication(
        version = OpenApiVersion.V3_0,
        openApiConfig = {
            postBuild = { openApi, _ ->
                openApi.components?.schemas?.values?.forEach { it.contentEncoding = "base64" }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        body shouldContain "\"format\" : \"byte\""
        body shouldNotContain "\"contentEncoding\""
    }


    @Test
    fun `v3_1 keeps contentEncoding unchanged`() = versionTestApplication(
        version = OpenApiVersion.V3_1,
        openApiConfig = {
            postBuild = { openApi, _ ->
                openApi.components?.schemas?.values?.forEach { it.contentEncoding = "base64" }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        body shouldContain "\"contentEncoding\" : \"base64\""
        body shouldNotContain "\"format\" : \"byte\""
    }


    @Test
    fun `v3_0 transforms contentMediaType to format binary`() = versionTestApplication(
        version = OpenApiVersion.V3_0,
        openApiConfig = {
            postBuild = { openApi, _ ->
                openApi.components?.schemas?.values?.forEach { it.contentMediaType = "application/octet-stream" }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        body shouldContain "\"format\" : \"binary\""
        body shouldNotContain "\"contentMediaType\""
    }


    @Test
    fun `v3_1 keeps contentMediaType unchanged`() = versionTestApplication(
        version = OpenApiVersion.V3_1,
        openApiConfig = {
            postBuild = { openApi, _ ->
                openApi.components?.schemas?.values?.forEach { it.contentMediaType = "application/octet-stream" }
            }
        }
    ) { client ->
        val body = client.get("/api.json").bodyAsText()
        body shouldContain "\"contentMediaType\" : \"application/octet-stream\""
        body shouldNotContain "\"format\" : \"binary\""
    }

    private fun versionTestApplication(
        version: OpenApiVersion,
        format: OutputFormat = OutputFormat.JSON,
        openApiConfig: OpenApiPluginConfig.() -> Unit = {},
        block: suspend (HttpClient) -> Unit,
    ) {
        testApplication {
            val client = createClient { followRedirects = false }
            install(OpenApi) {
                outputFormat = format
                openApiVersion = version
                openApiConfig()
            }
            routing {
                val suffix = if (format == OutputFormat.YAML) "yaml" else "json"
                route("api.$suffix") {
                    openApi()
                }
                // Route with a nullable field to exercise nullable schema handling
                get("hello", {
                    response {
                        HttpStatusCode.OK to {
                            body<NullableBody>()
                        }
                    }
                }) {
                    call.respondText("Hello")
                }
                // Route with exclusive min/max constraints to exercise exclusive bounds handling
                get("bounded", {
                    response {
                        HttpStatusCode.OK to {
                            body<BoundedBody>()
                        }
                    }
                }) {
                    call.respondText("Bounded")
                }
            }
            block(client)
        }
    }
}
