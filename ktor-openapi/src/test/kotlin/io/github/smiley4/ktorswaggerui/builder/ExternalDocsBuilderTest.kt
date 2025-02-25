package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktoropenapi.builder.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktoropenapi.data.ExternalDocsData
import io.github.smiley4.ktoropenapi.config.ExternalDocsConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.ExternalDocumentation

class ExternalDocsBuilderTest : StringSpec({

    "default external docs object" {
        buildExternalDocsObject {}.also { docs ->
            docs.url shouldBe "/"
            docs.description shouldBe null
        }
    }

    "complete server object" {
        buildExternalDocsObject {
            url = "Test URL"
            description = "Test Description"
        }.also { docs ->
            docs.url shouldBe "Test URL"
            docs.description shouldBe "Test Description"
        }
    }

}) {

    companion object {

        private fun buildExternalDocsObject(builder: ExternalDocsConfig.() -> Unit): ExternalDocumentation {
            return ExternalDocumentationBuilder().build(ExternalDocsConfig().apply(builder).build(ExternalDocsData.DEFAULT))
        }

    }

}
