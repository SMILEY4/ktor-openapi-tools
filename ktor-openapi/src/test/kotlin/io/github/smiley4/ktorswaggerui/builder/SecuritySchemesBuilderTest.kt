package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktoropenapi.builder.openapi.OAuthFlowsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.SecuritySchemesBuilder
import io.github.smiley4.ktoropenapi.config.AuthKeyLocation
import io.github.smiley4.ktoropenapi.config.AuthScheme
import io.github.smiley4.ktoropenapi.config.AuthType
import io.github.smiley4.ktoropenapi.data.SecuritySchemeData
import io.github.smiley4.ktoropenapi.config.SecuritySchemeConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.swagger.v3.oas.models.security.SecurityScheme

class SecuritySchemesBuilderTest : StringSpec({

    "test empty" {
        buildSecuritySchemeObjects(mapOf()).also { schemes ->
            schemes.shouldBeEmpty()
        }
    }

    "test default security scheme object" {
        buildSecuritySchemeObjects(mapOf("TestAuth" to {})).also { schemes ->
            schemes.keys shouldContainExactlyInAnyOrder listOf("TestAuth")
            schemes["TestAuth"]!!.also { scheme ->
                scheme.type shouldBe null
                scheme.description shouldBe null
                scheme.name shouldBe null
                scheme.`$ref` shouldBe null
                scheme.`in` shouldBe null
                scheme.scheme shouldBe null
                scheme.bearerFormat shouldBe null
                scheme.flows shouldBe null
                scheme.openIdConnectUrl shouldBe null
                scheme.extensions shouldBe null
            }
        }
    }

    "test basic security scheme objects" {
        buildSecuritySchemeObjects(mapOf(
            "TestAuth1" to {
                type = AuthType.HTTP
                scheme = AuthScheme.BASIC
            },
            "TestAuth2" to {
                type = AuthType.API_KEY
                location = AuthKeyLocation.COOKIE
            },
            "TestAuth3" to {
                type = AuthType.API_KEY
                location = AuthKeyLocation.QUERY
                name = "myQueryParam"
            }
        )).also { schemes ->
            schemes.keys shouldContainExactlyInAnyOrder listOf("TestAuth1", "TestAuth2", "TestAuth3")
            schemes["TestAuth1"]!!.also { scheme ->
                scheme.name shouldBe null
                scheme.type shouldBe SecurityScheme.Type.HTTP
                scheme.scheme shouldBe "Basic"
            }
            schemes["TestAuth2"]!!.also { scheme ->
                scheme.name shouldBe "TestAuth2"
                scheme.type shouldBe SecurityScheme.Type.APIKEY
                scheme.scheme shouldBe null
            }
            schemes["TestAuth3"]!!.also { scheme ->
                scheme.name shouldBe "myQueryParam"
                scheme.type shouldBe SecurityScheme.Type.APIKEY
                scheme.scheme shouldBe null
            }
        }
    }

    "test complete security scheme object" {
        buildSecuritySchemeObjects(mapOf("TestAuth" to {
            type = AuthType.HTTP
            location = AuthKeyLocation.COOKIE
            scheme = AuthScheme.BASIC
            bearerFormat = "test"
            openIdConnectUrl = "Test IOD-Connect URL"
            description = "Test Description"
            flows {
                implicit {
                    authorizationUrl = "Implicit Auth Url"
                    tokenUrl = "Implicity Token Url"
                    refreshUrl = "Implicity Token Url"
                    scopes = mapOf(
                        "implicit1" to "scope1",
                        "implicit2" to "scope2"
                    )
                }
                password {
                    authorizationUrl = "Password Auth Url"
                    tokenUrl = "Password Token Url"
                    refreshUrl = "Password Token Url"
                    scopes = mapOf(
                        "password1" to "scope1",
                        "password2" to "scope2"
                    )
                }
                clientCredentials {
                    authorizationUrl = "ClientCredentials Auth Url"
                    tokenUrl = "ClientCredentials Token Url"
                    refreshUrl = "ClientCredentials Token Url"
                    scopes = mapOf(
                        "clientCredentials1" to "scope1",
                        "clientCredentials2" to "scope2"
                    )
                }
                authorizationCode {
                    authorizationUrl = "AuthorizationCode Auth Url"
                    tokenUrl = "AuthorizationCode Token Url"
                    refreshUrl = "AuthorizationCode Token Url"
                    scopes = mapOf(
                        "authorizationCode1" to "scope1",
                        "authorizationCode2" to "scope2"
                    )
                }
            }
        })).also { schemes ->
            schemes.keys shouldContainExactlyInAnyOrder listOf("TestAuth")
            schemes["TestAuth"]!!.also { scheme ->
                scheme.name shouldBe null
                scheme.type shouldBe SecurityScheme.Type.HTTP
                scheme.`in` shouldBe SecurityScheme.In.COOKIE
                scheme.scheme shouldBe "Basic"
                scheme.bearerFormat shouldBe "test"
                scheme.openIdConnectUrl shouldBe "Test IOD-Connect URL"
                scheme.description shouldBe "Test Description"
                scheme.flows
                    .also { it.shouldNotBeNull() }
                    ?.also { flows ->
                        flows.implicit
                            .also { it.shouldNotBeNull() }
                            ?.also { implicit ->
                                implicit.authorizationUrl shouldBe "Implicit Auth Url"
                                implicit.tokenUrl shouldBe "Implicity Token Url"
                                implicit.refreshUrl shouldBe "Implicity Token Url"
                                implicit.scopes
                                    .also { it.shouldNotBeNull() }
                                    ?.also { scopes ->
                                        scopes.keys shouldContainExactlyInAnyOrder listOf("implicit1", "implicit2")
                                        scopes["implicit1"] shouldBe "scope1"
                                        scopes["implicit2"] shouldBe "scope2"
                                    }
                            }
                        flows.password
                            .also { it.shouldNotBeNull() }
                            ?.also { password ->
                                password.authorizationUrl shouldBe "Password Auth Url"
                                password.tokenUrl shouldBe "Password Token Url"
                                password.refreshUrl shouldBe "Password Token Url"
                                password.scopes
                                    .also { it.shouldNotBeNull() }
                                    ?.also { scopes ->
                                        scopes.keys shouldContainExactlyInAnyOrder listOf("password1", "password2")
                                        scopes["password1"] shouldBe "scope1"
                                        scopes["password2"] shouldBe "scope2"
                                    }
                            }
                        flows.clientCredentials
                            .also { it.shouldNotBeNull() }
                            ?.also { clientCredentials ->
                                clientCredentials.authorizationUrl shouldBe "ClientCredentials Auth Url"
                                clientCredentials.tokenUrl shouldBe "ClientCredentials Token Url"
                                clientCredentials.refreshUrl shouldBe "ClientCredentials Token Url"
                                clientCredentials.scopes
                                    .also { it.shouldNotBeNull() }
                                    ?.also { scopes ->
                                        scopes.keys shouldContainExactlyInAnyOrder listOf("clientCredentials1", "clientCredentials2")
                                        scopes["clientCredentials1"] shouldBe "scope1"
                                        scopes["clientCredentials2"] shouldBe "scope2"
                                    }
                            }
                        flows.authorizationCode
                            .also { it.shouldNotBeNull() }
                            ?.also { authorizationCode ->
                                authorizationCode.authorizationUrl shouldBe "AuthorizationCode Auth Url"
                                authorizationCode.tokenUrl shouldBe "AuthorizationCode Token Url"
                                authorizationCode.refreshUrl shouldBe "AuthorizationCode Token Url"
                                authorizationCode.scopes
                                    .also { it.shouldNotBeNull() }
                                    ?.also { scopes ->
                                        scopes.keys shouldContainExactlyInAnyOrder listOf("authorizationCode1", "authorizationCode2")
                                        scopes["authorizationCode1"] shouldBe "scope1"
                                        scopes["authorizationCode2"] shouldBe "scope2"
                                    }
                            }
                    }
            }
        }
    }

}) {

    companion object {

        private fun buildSecuritySchemeObjects(builders: Map<String, SecuritySchemeConfig.() -> Unit>): Map<String, SecurityScheme> {
            return SecuritySchemesBuilder(
                oAuthFlowsBuilder = OAuthFlowsBuilder()
            ).build(builders.map { (name, entry) -> SecuritySchemeConfig(name).apply(entry).build(
                SecuritySchemeData.DEFAULT) })
        }

    }

}
