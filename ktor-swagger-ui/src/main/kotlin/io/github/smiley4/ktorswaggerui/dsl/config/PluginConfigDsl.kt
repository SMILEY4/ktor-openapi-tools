package io.github.smiley4.ktorswaggerui.dsl.config

import io.github.smiley4.ktorswaggerui.data.DataUtils.merge
import io.github.smiley4.ktorswaggerui.data.DataUtils.mergeDefault
import io.github.smiley4.ktorswaggerui.data.OutputFormat
import io.github.smiley4.ktorswaggerui.data.PathFilter
import io.github.smiley4.ktorswaggerui.data.PluginConfigData
import io.github.smiley4.ktorswaggerui.data.PostBuild
import io.github.smiley4.ktorswaggerui.data.ServerData
import io.github.smiley4.ktorswaggerui.data.SpecAssigner
import io.github.smiley4.ktorswaggerui.dsl.OpenApiDslMarker
import io.ktor.server.routing.RouteSelector
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * Main-Configuration of the "SwaggerUI"-Plugin
 */
@OpenApiDslMarker
class PluginConfigDsl {

    companion object {
        const val DEFAULT_SPEC_ID = "api"
    }


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    fun info(block: OpenApiInfo.() -> Unit) {
        info = OpenApiInfo().apply(block)
    }

    private var info = OpenApiInfo()


    /**
     * OpenAPI external docs configuration - link and description of an external documentation
     */
    fun externalDocs(block: OpenApiExternalDocs.() -> Unit) {
        externalDocs = OpenApiExternalDocs().apply(block)
    }

    private var externalDocs = OpenApiExternalDocs()


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: OpenApiServer.() -> Unit) {
        servers.add(OpenApiServer().apply(block))
    }

    private val servers = mutableListOf<OpenApiServer>()


    /**
     * Swagger-UI configuration
     */
    fun swagger(block: SwaggerUIDsl.() -> Unit) {
        swaggerUI = SwaggerUIDsl().apply(block)
    }

    private var swaggerUI = SwaggerUIDsl()


    /**
     * Configuration for security and authentication.
     */
    fun security(block: OpenApiSecurity.() -> Unit) {
        security.apply(block)
    }

    private val security = OpenApiSecurity()


    /**
     * Configuration for openapi-tags
     */
    fun tags(block: OpenApiTags.() -> Unit) {
        tags.also(block)
    }

    private val tags = OpenApiTags()


    /**
     * Configure schemas
     */
    fun schemas(block: SchemaConfig.() -> Unit) {
        schemaConfig.also(block)
    }

    private val schemaConfig = SchemaConfig()


    /**
     * Configure examples
     */
    fun examples(block: ExampleConfig.() -> Unit) {
        exampleConfig.apply(block)
    }

    private val exampleConfig = ExampleConfig()


    /**
     * Configure specific separate specs
     */
    fun spec(specId: String, block: PluginConfigDsl.() -> Unit) {
        specConfigs[specId] = PluginConfigDsl().apply(block)
    }

    private val specConfigs = mutableMapOf<String, PluginConfigDsl>()


    /**
     * Assigns routes without an [io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute.specId]] to a specified openapi-spec.
     */
    var specAssigner: SpecAssigner? = PluginConfigData.DEFAULT.specAssigner


    /**
     * Filter to apply to all routes. Return 'false' for routes to not include them in the OpenApi-Spec and Swagger-UI.
     * The url of the paths are already split at '/'.
     */
    var pathFilter: PathFilter? = PluginConfigData.DEFAULT.pathFilter


    /**
     * List of all [RouteSelector] types in that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectors: Set<KClass<*>> = PluginConfigData.DEFAULT.ignoredRouteSelectors

    /**
     * List of all [RouteSelector] class names that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectorClassNames: Set<String> = emptySet()

    /**
     * The format of the generated api-spec
     */
    var outputFormat: OutputFormat = PluginConfigData.DEFAULT.outputFormat


    /**
     * Invoked after generating the openapi-spec. Can be to e.g. further customize the spec.
     */
    var postBuild: PostBuild? = null


    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: PluginConfigData): PluginConfigData {
        val securityConfig = security.build(base.securityConfig)
        return PluginConfigData(
            info = info.build(base.info),
            externalDocs = externalDocs.build(base.externalDocs),
            servers = buildList {
                addAll(base.servers)
                addAll(servers.map { it.build(ServerData.DEFAULT) })
            },
            swagger = swaggerUI.build(base.swagger),
            securityConfig = securityConfig,
            tagsConfig = tags.build(base.tagsConfig),
            schemaConfig = schemaConfig.build(base.schemaConfig, securityConfig),
            exampleConfig = exampleConfig.build(securityConfig),
            specAssigner = merge(base.specAssigner, specAssigner) ?: PluginConfigData.DEFAULT.specAssigner,
            pathFilter = merge(base.pathFilter, pathFilter) ?: PluginConfigData.DEFAULT.pathFilter,
            ignoredRouteSelectors = buildSet {
                addAll(base.ignoredRouteSelectors)
                addAll(ignoredRouteSelectors)
            },
            ignoredRouteSelectorClassNames = buildSet {
                addAll(base.ignoredRouteSelectorClassNames)
                addAll(ignoredRouteSelectorClassNames)
            },
            specConfigs = mutableMapOf(),
            postBuild = merge(base.postBuild, postBuild),
            outputFormat = mergeDefault(base.outputFormat, outputFormat, PluginConfigData.DEFAULT.outputFormat)
        ).also {
            specConfigs.forEach { (specId, config) ->
                it.specConfigs[specId] = config.build(it)
            }
        }
    }
}
