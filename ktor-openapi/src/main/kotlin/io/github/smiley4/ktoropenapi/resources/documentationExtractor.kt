@file:OptIn(ExperimentalSerializationApi::class)

package io.github.smiley4.ktoropenapi.resources

import io.github.smiley4.ktoropenapi.OpenApiPlugin
import io.github.smiley4.ktoropenapi.config.ParameterLocation
import io.github.smiley4.ktoropenapi.config.RouteConfig
import io.github.smiley4.ktoropenapi.config.descriptors.KTypeDescriptor
import io.github.smiley4.ktoropenapi.config.descriptors.SerialTypeDescriptor
import io.ktor.resources.serialization.ResourcesFormat
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.elementNames
import kotlin.reflect.KType

private data class ParameterData(
    val name: String,
    val descriptor: SerialDescriptor,
    val optional: Boolean,
    val location: ParameterLocation
)


/**
 * Intended for internal use.
 * Automatically extract route configuration from the given resource class.
 */
fun <T> extractTypesafeDocumentation(serializer: KSerializer<T>, resourcesFormat: ResourcesFormat): RouteConfig.() -> Unit {
    return extractTypesafeDocumentation(serializer, null, resourcesFormat)
}


/**
 * Intended for internal use.
 * Automatically extract route configuration from the given resource class.
 */
fun <T> extractTypesafeDocumentation(
    serializer: KSerializer<T>,
    bodyType: KType?,
    resourcesFormat: ResourcesFormat
): RouteConfig.() -> Unit {
    if (!OpenApiPlugin.config.autoDocumentResourcesRoutes) {
        return {}
    }
    // Note: typesafe routing only defines information about path & query parameters & request body - no other information is available
    val path = resourcesFormat.encodeToPathPattern(serializer)
    return {
        request {
            collectParameters(serializer.descriptor, path).forEach { parameter ->
                parameter(parameter.location, parameter.name, SerialTypeDescriptor(parameter.descriptor)) {
                    required = !parameter.optional
                }
            }
            bodyType?.also {
                body(KTypeDescriptor(it)) {}
            }
        }
    }
}


private fun collectParameters(descriptor: SerialDescriptor, path: String): List<ParameterData> {
    val parameters = mutableListOf<ParameterData>()

    descriptor.elementNames.forEach { name ->
        val index = descriptor.getElementIndex(name)
        val elementDescriptor = descriptor.getElementDescriptor(index)
        if (!elementDescriptor.isInline && elementDescriptor.kind is StructureKind.CLASS) {
            parameters.addAll(collectParameters(elementDescriptor, path))
        } else {
            val location = getLocation(name, path)
            parameters.add(
                ParameterData(
                    name = name,
                    descriptor = elementDescriptor,
                    optional = when (location) {
                        ParameterLocation.PATH -> path.contains("{$name?}")
                        ParameterLocation.QUERY -> elementDescriptor.isNullable || descriptor.isElementOptional(index)
                        else -> false
                    },
                    location = location
                )
            )
        }
    }

    return parameters
}


/**
 * path contains parameter with given name -> "PATH"
 * path does not contain parameter with given name -> "QUERY"
 */
private fun getLocation(name: String, path: String): ParameterLocation {
    return if (path.contains("{$name}") || path.contains("{$name?}") || path.contains("{$name...}")) {
        ParameterLocation.PATH
    } else {
        ParameterLocation.QUERY
    }
}
