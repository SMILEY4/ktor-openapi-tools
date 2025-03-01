package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktoropenapi.builder.route.RouteDocumentationMerger
import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlin.reflect.typeOf

class RouteDocumentationMergerTest : StringSpec({

    "merge empty routes" {
        merge(
            route {},
            route {}
        ).also { route ->
            route.tags.shouldBeEmpty()
            route.summary shouldBe null
            route.description shouldBe null
            route.operationId shouldBe null
            route.deprecated shouldBe false
            route.hidden shouldBe false
            route.securitySchemeNames.shouldBeEmpty()
            route.protected shouldBe null
            route.getRequest().also { requests ->
                requests.parameters.shouldBeEmpty()
                requests.getBody() shouldBe null
            }
            route.getResponses().also { responses ->
                responses.getResponses().shouldBeEmpty()
            }
        }
    }

    "merge complete routes" {
        merge(
            route {
                specName = "test-spec-a"
                tags = listOf("a1", "a2")
                summary = "Summary A"
                description = "Description A"
                operationId = "operationA"
                securitySchemeNames = listOf("securitySchemeNameA1", "securitySchemeNameA2")
                deprecated = true
                hidden = false
                protected = true
                request {
                    queryParameter("query", KTypeDescriptor(typeOf<String>())) {}
                    pathParameter("pathA1", KTypeDescriptor(typeOf<String>())) {}
                    pathParameter("pathA2", KTypeDescriptor(typeOf<String>())) {}
                    body(KTypeDescriptor(typeOf<String>())) {
                        description = "body A"
                    }
                }
                response {
                    "a1" to { description = "response a1" }
                    "a2" to { description = "response a1" }
                }
            },
            route {
                specName = "test-spec-b"
                tags = listOf("b1", "b2")
                summary = "Summary B"
                description = "Description B"
                operationId = "operationB"
                securitySchemeNames = listOf("securitySchemeNameB1", "securitySchemeNameB2")
                deprecated = false
                hidden = true
                protected = false
                request {
                    queryParameter("query", KTypeDescriptor(typeOf<String>())) {}
                    pathParameter("pathB1", KTypeDescriptor(typeOf<String>())) {}
                    pathParameter("pathB2", KTypeDescriptor(typeOf<String>())) {}
                    body(KTypeDescriptor(typeOf<String>())) {
                        description = "body B"
                    }
                }
                response {
                    "b1" to { description = "response b1" }
                    "b2" to { description = "response b1" }
                }
            }
        ).also { route ->
            route.specName shouldBe "test-spec-a"
            route.tags shouldContainExactlyInAnyOrder listOf("a1", "a2", "b1", "b2")
            route.summary shouldBe "Summary A"
            route.description shouldBe "Description A"
            route.operationId shouldBe "operationA"
            route.deprecated shouldBe true
            route.hidden shouldBe true
            route.securitySchemeNames shouldContainExactlyInAnyOrder listOf(
                "securitySchemeNameA1",
                "securitySchemeNameA2",
                "securitySchemeNameB1",
                "securitySchemeNameB2"
            )
            route.protected shouldBe true
            route.getRequest().also { requests ->
                requests.parameters.map { it.name } shouldContainExactlyInAnyOrder listOf(
                    "query",
                    "pathA1",
                    "pathA2",
                    "pathB1",
                    "pathB2"
                )
                requests.getBody()
                    .also { it shouldNotBe null }
                    ?.also { it.description shouldBe "body A" }
            }
            route.getResponses().also { responses ->
                responses.getResponses().map { it.statusCode } shouldContainExactlyInAnyOrder listOf(
                    "b1", "b2", "a1", "a2"
                )
            }
        }
    }

}) {

    companion object {

        fun route(builder: RouteConfig.() -> Unit): RouteConfig {
            return RouteConfig().apply(builder)
        }

        fun merge(a: RouteConfig, b: RouteConfig): RouteConfig {
            return RouteDocumentationMerger().merge(a, b)
        }

    }

}
