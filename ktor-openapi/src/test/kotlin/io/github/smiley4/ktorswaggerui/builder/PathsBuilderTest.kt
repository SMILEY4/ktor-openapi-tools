package io.github.smiley4.ktorswaggerui.builder

import io.github.smiley4.ktoropenapi.builder.example.ExampleContext
import io.github.smiley4.ktoropenapi.builder.example.ExampleContextImpl
import io.github.smiley4.ktoropenapi.builder.openapi.ContentBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ExternalDocumentationBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.HeaderBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OperationBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.OperationTagsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ParameterBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.PathBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.PathsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.RequestBodyBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ResponseBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ResponsesBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.SecurityRequirementsBuilder
import io.github.smiley4.ktoropenapi.builder.openapi.ServerBuilder
import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContext
import io.github.smiley4.ktoropenapi.builder.schema.SchemaContextImpl
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.github.smiley4.ktoropenapi.config.OpenApiPluginConfig
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.ktor.http.HttpMethod
import io.swagger.v3.oas.models.Paths

class PathsBuilderTest : StringSpec({

    "simple paths" {
        val routes = listOf(
            route(HttpMethod.Get, "/"),
            route(HttpMethod.Delete, "/test/path"),
            route(HttpMethod.Post, "/other/test/route")
        )
        val schemaContext = schemaContext(routes, defaultPluginConfig)
        val exampleContext = exampleContext(routes, defaultPluginConfig)
        buildPathsObject(routes, schemaContext, exampleContext).also { paths ->
            paths shouldHaveSize 3
            paths.keys shouldContainExactlyInAnyOrder listOf(
                "/",
                "/test/path",
                "/other/test/route"
            )
            paths["/"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.delete.shouldNotBeNull()
            paths["/other/test/route"]!!.post.shouldNotBeNull()
        }
    }

    "merge paths" {
        val config = defaultPluginConfig
        val routes = listOf(
            route(HttpMethod.Get, "/different/path"),
            route(HttpMethod.Get, "/test/path"),
            route(HttpMethod.Post, "/test/path"),
        )
        val schemaContext = schemaContext(routes, config)
        val exampleContext = exampleContext(routes, config)
        buildPathsObject(routes, schemaContext, exampleContext, config).also { paths ->
            paths shouldHaveSize 2
            paths.keys shouldContainExactlyInAnyOrder listOf(
                "/different/path",
                "/test/path"
            )
            paths["/different/path"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.get.shouldNotBeNull()
            paths["/test/path"]!!.post.shouldNotBeNull()
        }
    }

    "custom root path" {
        val config = defaultPluginConfig.apply {
            rootPath = "custom/root/path"
        }
        val routes = listOf(
            route(HttpMethod.Get, "/different/path"),
            route(HttpMethod.Get, "/test/path"),
            route(HttpMethod.Post, "/test/path"),
        )
        val schemaContext = schemaContext(routes, config)
        val exampleContext = exampleContext(routes, config)
        buildPathsObject(routes, schemaContext, exampleContext, config).also { paths ->
            paths shouldHaveSize 2
            paths.keys shouldContainExactlyInAnyOrder listOf(
                "custom/root/path/different/path",
                "custom/root/path/test/path"
            )
            paths["custom/root/path/different/path"]!!.get.shouldNotBeNull()
            paths["custom/root/path/test/path"]!!.get.shouldNotBeNull()
            paths["custom/root/path/test/path"]!!.post.shouldNotBeNull()
        }
    }

}) {

    companion object {

        private fun route(method: HttpMethod, url: String) = RouteMeta(
            path = url,
            method = method,
            documentation = RouteConfig().build(),
            protected = false,
            isWebhook = false,
        )

        private val defaultPluginConfig = OpenApiPluginConfig()

        private fun schemaContext(routes: List<RouteMeta>, pluginConfig: OpenApiPluginConfig): SchemaContext {
            val pluginConfigData = pluginConfig.build(OpenApiPluginData.DEFAULT, null)
            return SchemaContextImpl(pluginConfigData.schemaConfig).also {
                it.addGlobal(pluginConfigData.schemaConfig)
                it.add(routes)
            }
        }

        private fun exampleContext(routes: List<RouteMeta>, pluginConfig: OpenApiPluginConfig): ExampleContext {
            val pluginConfigData = pluginConfig.build(OpenApiPluginData.DEFAULT, null)
            return ExampleContextImpl(pluginConfigData.exampleConfig.exampleEncoder).also {
                it.addShared(pluginConfigData.exampleConfig)
                it.add(routes)
            }
        }

        private fun buildPathsObject(
            routes: Collection<RouteMeta>,
            schemaContext: SchemaContext,
            exampleContext: ExampleContext,
            pluginConfig: OpenApiPluginConfig = defaultPluginConfig
        ): Paths {
            val pluginConfigData = pluginConfig.build(OpenApiPluginData.DEFAULT, null)
            return PathsBuilder(
                config = pluginConfigData,
                pathBuilder = PathBuilder(
                    operationBuilder = OperationBuilder(
                        operationTagsBuilder = OperationTagsBuilder(pluginConfigData),
                        parameterBuilder = ParameterBuilder(
                            schemaContext = schemaContext,
                            exampleContext = exampleContext
                        ),
                        requestBodyBuilder = RequestBodyBuilder(
                            contentBuilder = ContentBuilder(
                                schemaContext = schemaContext,
                                exampleContext = exampleContext,
                                headerBuilder = HeaderBuilder(schemaContext)
                            )
                        ),
                        responsesBuilder = ResponsesBuilder(
                            responseBuilder = ResponseBuilder(
                                headerBuilder = HeaderBuilder(schemaContext),
                                contentBuilder = ContentBuilder(
                                    schemaContext = schemaContext,
                                    exampleContext = exampleContext,
                                    headerBuilder = HeaderBuilder(schemaContext)
                                )
                            ),
                            config = pluginConfigData
                        ),
                        securityRequirementsBuilder = SecurityRequirementsBuilder(pluginConfigData),
                        externalDocumentationBuilder = ExternalDocumentationBuilder(),
                        serverBuilder = ServerBuilder()
                    )
                )
            ).build(routes)
        }

    }

}
