package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktoropenapi.builder.openapi.TagBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.TagExternalDocumentationBuilder
import io.github.smiley4.ktoropenapi.data.TagData
import io.github.smiley4.ktoropenapi.config.TagConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.tags.Tag


class TagsBuilderTest : StringSpec({

    "empty tag object" {
        buildTagObject("test-tag") {}.also { tag ->
            tag.name shouldBe "test-tag"
            tag.description shouldBe null
            tag.externalDocs shouldBe null
            tag.extensions shouldBe null
        }
    }

    "full tag object" {
        buildTagObject("test-tag") {
            description = "Description of tag"
            externalDocDescription = "Description of external docs"
            externalDocUrl = "example.com"
        }.also { tag ->
            tag.name shouldBe "test-tag"
            tag.description shouldBe "Description of tag"
            tag.externalDocs
                .also { docs -> docs.shouldNotBeNull() }
                ?.also { docs ->
                    docs.description shouldBe "Description of external docs"
                    docs.url shouldBe "example.com"
                    docs.extensions shouldBe null
                }
            tag.extensions shouldBe null
        }
    }

    "tag object with x-displayName" {
        buildTagObject("test-tag-123") {
            description = "Description of tag"
            displayName = "Custom Display Name"
        }.also { tag ->
            tag.name shouldBe "test-tag-123"
            tag.description shouldBe "Description of tag"
            tag.extensions.shouldNotBeNull()
            tag.extensions["x-displayName"] shouldBe "Custom Display Name"
        }
    }

}) {

    companion object {

        private fun buildTagObject(name: String, builder: TagConfig.() -> Unit): Tag {
            return TagBuilder(
                tagExternalDocumentationBuilder = TagExternalDocumentationBuilder()
            ).build(TagConfig(name).apply(builder).build(TagData.DEFAULT))
        }

    }

}
