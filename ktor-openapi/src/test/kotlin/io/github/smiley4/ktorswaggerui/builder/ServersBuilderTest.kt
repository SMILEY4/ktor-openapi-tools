package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktoropenapi.builder.openapi.ServerBuilder
import io.github.smiley4.ktoropenapi.data.ServerData
import io.github.smiley4.ktoropenapi.config.ServerConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.servers.Server

class ServersBuilderTest : StringSpec({

    "default server object" {
        buildServerObject {}.also { server ->
            server.url shouldBe "/"
            server.description shouldBe null
            server.variables shouldBe null
            server.extensions shouldBe null
        }
    }

    "complete server object" {
        buildServerObject {
            url = "Test URL"
            description = "Test Description"
            variable("version") {
                description = "the version of the api"
                default = "2"
                enum = setOf("1", "2", "3")
            }
            variable("region") {
                description = "the region of the api"
                default = "somewhere"
                enum = setOf("somewhere", "else")
            }
        }.also { server ->
            server.url shouldBe "Test URL"
            server.description shouldBe "Test Description"
            server.variables.keys shouldContainExactlyInAnyOrder listOf("version", "region")
            server.variables["version"]!!.also { variable ->
                variable.description shouldBe "the version of the api"
                variable.default shouldBe "2"
                variable.enum shouldContainExactlyInAnyOrder listOf("1", "2", "3")
            }
            server.variables["region"]!!.also { variable ->
                variable.description shouldBe "the region of the api"
                variable.default shouldBe "somewhere"
                variable.enum shouldContainExactlyInAnyOrder listOf("somewhere", "else")
            }
            server.extensions shouldBe null
        }
    }

}) {

    companion object {

        private fun buildServerObject(builder: ServerConfig.() -> Unit): Server {
            return ServerBuilder().build(ServerConfig().apply(builder).build(ServerData.DEFAULT))
        }

    }

}
