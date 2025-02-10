package io.github.smiley4.ktoropenapi

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.smiley4.ktoropenapi.builder.OpenApiSpecBuilder
import io.github.smiley4.ktoropenapi.builder.route.RouteCollector
import io.github.smiley4.ktoropenapi.builder.route.RouteMeta
import io.github.smiley4.ktoropenapi.config.OpenApiPluginConfig
import io.github.smiley4.ktoropenapi.config.OutputFormat
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.MonitoringEvent
import io.ktor.server.application.plugin
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.get

private val logger = KotlinLogging.logger {}


/**
 * The OpenAPI Ktor Plugin
 */
val OpenApi: ApplicationPlugin<OpenApiPluginConfig> = createApplicationPlugin("OpenApi", ::OpenApiPluginConfig) {
    OpenApiPlugin.config = pluginConfig.build(OpenApiPluginData.DEFAULT, getRootPath(application))
    on(MonitoringEvent(ApplicationStarted)) { application ->
        try {
            OpenApiPlugin.generateOpenApiSpecs(application)
        } catch (e: Exception) {
            logger.error(e) { "Error during application startup in openapi-plugin" }
        }
    }
}

private fun getRootPath(application: Application): String? {
    if (application.rootPath.isNotBlank()) {
        return application.rootPath
    }
    return application.environment.config.propertyOrNull("ktor.deployment.rootPath")?.getString()
}


/**
 * Provides functionality to interact with OpenAPI specification generation.
 */
object OpenApiPlugin {

    internal var config = OpenApiPluginData.DEFAULT

    private val openApiSpecs = mutableMapOf<String, Pair<String, OutputFormat>>()


    /**
     * Generates new OpenAPI specification for the given application. Replaces previously generated specifications.
     */
    fun generateOpenApiSpecs(application: Application) {
        val routes = RouteCollector().collect({ application.plugin(RoutingRoot) }, config) + webhooks.map { (name, entry) ->
            RouteMeta(
                method = entry.first,
                path = name,
                documentation = entry.second.build(),
                protected = false,
                isWebhook = true
            )
        }
        val specs = OpenApiSpecBuilder().build(config, routes)
        openApiSpecs.clear()
        openApiSpecs.putAll(specs)
    }


    /**
     * Provides the generated specification with the given name. Throws if no specification with the given name exists (yet).
     * @param name the name of the specification to get. [OpenApiPluginConfig.DEFAULT_SPEC_ID] if only one specification is used and no name has been given
     * @return the OpenAPI specification with the given name as a string.
     */
    fun getOpenApiSpec(name: String): String = openApiSpecs[name]?.first
        ?: throw IllegalArgumentException("No OpenAPI documentation exists with name '$name'")


    /**
     * Provides the format of the generated specification with the given name. Throws if no specification with the given name exists (yet).
     * @param name the name of the specification to get. [OpenApiPluginConfig.DEFAULT_SPEC_ID] if only one specification is used and no name has been given
     * @return the [OutputFormat] OpenAPI specification of the given name.
     */
    fun getOpenApiSpecFormat(name: String): OutputFormat = openApiSpecs[name]?.second
        ?: throw IllegalArgumentException("No OpenAPI documentation exists with name '$name'")

}


/**
 * Registers the route for serving an openapi-spec. When multiple specs are configured, the name of the one to serve has to be provided.
 * @param specName the name of the specification to get. [OpenApiPluginConfig.DEFAULT_SPEC_ID] if only one specification is used and no name has been given
 */
fun Route.openApi(specName: String = OpenApiPluginConfig.DEFAULT_SPEC_ID) {
    route({ hidden = true }) {
        get {
            val contentType = when (OpenApiPlugin.getOpenApiSpecFormat(specName)) {
                OutputFormat.JSON -> ContentType.Application.Json
                OutputFormat.YAML -> ContentType.Text.Plain
            }
            call.respondText(contentType, HttpStatusCode.OK) { OpenApiPlugin.getOpenApiSpec(specName) }
        }
    }
}
