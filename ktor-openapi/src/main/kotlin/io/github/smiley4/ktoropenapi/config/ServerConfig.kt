package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.ktoropenapi.data.DataUtils.merge
import io.github.smiley4.ktoropenapi.data.DataUtils.mergeDefault
import io.github.smiley4.ktoropenapi.data.ServerData

/**
 * An object representing a Server.
 */
@OpenApiDslMarker
class ServerConfig internal constructor() {

    /**
     * A URL to the target host. This URL supports Server Variables and MAY be relative, to indicate that the host location is relative to
     * the location where the OpenAPI document is being served
     */
    var url: String = ServerData.DEFAULT.url

    /**
     * An optional string describing the host designated by the URL
     */
    var description: String? = ServerData.DEFAULT.description

    private val variables = mutableMapOf<String, ServerVariableConfig>()


    /**
     * Adds a new server variable with the given name
     */
    fun variable(name: String, block: ServerVariableConfig.() -> Unit) {
        variables[name] = ServerVariableConfig(name).apply(block)
    }

    /**
     * Build the data object for this config.
     * @param base the base config to "inherit" from. Values from the base should be copied, replaced or merged together.
     */
    internal fun build(base: ServerData) = ServerData(
        url = mergeDefault(base.url, url, ServerData.DEFAULT.url),
        description = merge(base.description, description),
        variables = buildMap {
            base.variables.forEach { this[it.name] = it }
            variables.values.map { it.build() }.forEach { this[it.name] = it }
        }.values.toList()
    )

}
