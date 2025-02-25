package io.github.smiley4.ktorswaggerui.builder
import io.github.smiley4.ktoropenapi.builder.openapi.ContactBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.InfoBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.LicenseBuilder
import io.github.smiley4.ktoropenapi.data.InfoData
import io.github.smiley4.ktoropenapi.config.InfoConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.info.Info


class InfoBuilderTest : StringSpec({

    "empty info object" {
        buildInfoObject {}.also { info ->
            info.title shouldBe "API"
            info.version shouldBe "latest"
            info.description shouldBe null
            info.termsOfService shouldBe null
            info.contact shouldBe null
            info.license shouldBe null
            info.extensions shouldBe null
            info.summary shouldBe null
        }
    }

    "full info object" {
        buildInfoObject {
            title = "Test Api"
            version = "1.0"
            summary = "testing api"
            description = "Api for testing"
            termsOfService = "test-tos"
            contact {
                name = "Test Person"
                url = "example.com"
                email = "test.mail"

            }
            license {
                name = "Test License"
                url = "example.com"
                identifier = "Example"
            }
        }.also { info ->
            info.title shouldBe "Test Api"
            info.version shouldBe "1.0"
            info.summary shouldBe "testing api"
            info.description shouldBe "Api for testing"
            info.termsOfService shouldBe "test-tos"
            info.contact
                .also { contact -> contact.shouldNotBeNull() }
                ?.also { contact ->
                    contact.name shouldBe "Test Person"
                    contact.url shouldBe "example.com"
                    contact.email shouldBe "test.mail"
                }
            info.license
                .also { license -> license.shouldNotBeNull() }
                ?.also { license ->
                    license.name shouldBe "Test License"
                    license.url shouldBe "example.com"
                    license.identifier shouldBe "Example"
                }
            info.extensions shouldBe null
        }
    }

}) {

    companion object {

        private fun buildInfoObject(builder: InfoConfig.() -> Unit): Info {
            return InfoBuilder(
                contactBuilder = ContactBuilder(),
                licenseBuilder = LicenseBuilder()
            ).build(InfoConfig().apply(builder).build(InfoData.DEFAULT))
        }

    }

}
