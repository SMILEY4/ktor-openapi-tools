package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.OpenApiPluginData
import io.github.smiley4.ktoropenapi.data.ServerData
import io.ktor.server.routing.RouteSelector
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import kotlin.reflect.KClass

/**
 * Main-Configuration of the "OpenApi"-Plugin
 */
@OpenApiDslMarker
class OpenApiPluginConfig {

    companion object {
        const val DEFAULT_SPEC_ID = "api"
    }


    /**
     * OpenAPI info configuration - provides metadata about the API
     */
    fun info(block: InfoConfig.() -> Unit) {
        info = InfoConfig().apply(block)
    }

    private var info = InfoConfig()


    /**
     * OpenAPI external docs configuration - link and description of an external documentation
     */
    fun externalDocs(block: ExternalDocsConfig.() -> Unit) {
        externalDocs = ExternalDocsConfig().apply(block)
    }

    private var externalDocs = ExternalDocsConfig()


    /**
     * OpenAPI server configuration - an array of servers, which provide connectivity information to a target server
     */
    fun server(block: ServerConfig.() -> Unit) {
        servers.add(ServerConfig().apply(block))
    }

    private val servers = mutableListOf<ServerConfig>()


    /**
     * Configuration for security and authentication.
     */
    fun security(block: SecurityConfig.() -> Unit) {
        security.apply(block)
    }

    private val security = SecurityConfig()


    /**
     * Configuration for openapi-tags
     */
    fun tags(block: TagsConfig.() -> Unit) {
        tags.also(block)
    }

    private val tags = TagsConfig()


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
    fun spec(specName: String, block: OpenApiPluginConfig.() -> Unit) {
        specConfigs[specName] = OpenApiPluginConfig().apply(block)
    }

    private val specConfigs = mutableMapOf<String, OpenApiPluginConfig>()


    /**
     * Assigns routes without an [io.github.smiley4.ktoropenapi.config.RouteConfig.specName]] to a specified openapi-spec.
     */
    var specAssigner: SpecAssigner? = OpenApiPluginData.DEFAULT.specAssigner


    /**
     * Filter to apply to all routes. Return 'false' for routes to not include them in the OpenApi-Spec and Swagger-UI.
     * The url of the paths are already split at '/'.
     */
    var pathFilter: PathFilter? = OpenApiPluginData.DEFAULT.pathFilter


    /**
     * List of all [RouteSelector] types in that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectors: Set<KClass<*>> = OpenApiPluginData.DEFAULT.ignoredRouteSelectors


    /**
     * List of all [RouteSelector] class names that should be ignored in the resulting url of any route.
     */
    var ignoredRouteSelectorClassNames: Set<String> = emptySet()


    /**
     * The format of the generated api-spec
     */
    var outputFormat: OutputFormat = OpenApiPluginData.DEFAULT.outputFormat


    /**
     * Invoked after generating the openapi-spec. Can be to e.g. further customize the spec.
     */
    var postBuild: PostBuild? = null


    /**
     * Root path of the ktor-application to prepend to the paths. "null" to automatically fetch from ktor configuration.
     */
    var rootPath: String? = OpenApiPluginData.DEFAULT.rootPath


    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: OpenApiPluginData, ktorRootPath: String?): OpenApiPluginData {
        val securityConfig = security.build(base.securityConfig)
        return OpenApiPluginData(
            info = info.build(base.info),
            externalDocs = externalDocs.build(base.externalDocs),
            servers = buildList {
                addAll(base.servers)
                addAll(servers.map { it.build(ServerData.DEFAULT) })
            },
            securityConfig = securityConfig,
            tagsConfig = tags.build(base.tagsConfig),
            schemaConfig = schemaConfig.build(securityConfig),
            exampleConfig = exampleConfig.build(securityConfig),
            specAssigner = merge(base.specAssigner, specAssigner) ?: OpenApiPluginData.DEFAULT.specAssigner,
            pathFilter = merge(base.pathFilter, pathFilter) ?: OpenApiPluginData.DEFAULT.pathFilter,
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
            outputFormat = outputFormat,
            rootPath = merge(rootPath ?: ktorRootPath, base.rootPath)
        ).also {
            specConfigs.forEach { (specName, config) ->
                it.specConfigs[specName] = config.build(it, ktorRootPath)
            }
        }
    }

}
