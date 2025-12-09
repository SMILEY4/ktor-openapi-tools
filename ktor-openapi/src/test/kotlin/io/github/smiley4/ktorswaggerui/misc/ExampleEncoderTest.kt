package io.github.smiley4.ktorswaggerui.misc

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.ExampleEncoder
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.openApi
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.testing.testApplication
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ExampleEncoderTest : StringSpec({

    val json = Json {
        classDiscriminator = "_type"
        serializersModule = SerializersModule {
            contextual(LocalDate::class, object : KSerializer<LocalDate> {
                private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
                override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)
                override fun deserialize(decoder: Decoder) = LocalDate.parse(decoder.decodeString(), formatter)
                override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(formatter.format(value))
            })
        }
    }

    "ExampleEncoder.kotlinx should not fail with contextual type" {
        testApplication {
            install(OpenApi) {
                examples {
                    encoder(ExampleEncoder.kotlinx(json))
                }
            }
            routing {
                route("api.yml") {
                    openApi()
                }

                get("test", {
                    response {
                        HttpStatusCode.OK to {
                            body<LocalDate> {
                                example("example") {
                                    value = LocalDate.of(2025, 12, 9)
                                }
                            }
                        }
                    }
                }) {
                    call.respond(HttpStatusCode.OK)
                }
            }

            createClient {}.get("api.yml").bodyAsText() shouldNotBe "{}"
        }
    }
})
