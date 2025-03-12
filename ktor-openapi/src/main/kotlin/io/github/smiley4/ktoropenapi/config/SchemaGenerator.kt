@file:OptIn(ExperimentalUuidApi::class)

package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.schemakenerator.core.CoreSteps.addDiscriminatorProperty
import io.github.smiley4.schemakenerator.core.CoreSteps.addMissingSupertypeSubtypeRelations
import io.github.smiley4.schemakenerator.core.CoreSteps.handleNameAnnotation
import io.github.smiley4.schemakenerator.core.data.InitialTypeData
import io.github.smiley4.schemakenerator.reflection.ReflectionSteps.analyzeTypeUsingReflection
import io.github.smiley4.schemakenerator.reflection.ReflectionSteps.collectSubTypes
import io.github.smiley4.schemakenerator.reflection.analyzer.ReflectionCustomProvider
import io.github.smiley4.schemakenerator.reflection.analyzer.ReflectionTypeAnalyzerModule
import io.github.smiley4.schemakenerator.reflection.analyzer.ReflectionTypeMatcher
import io.github.smiley4.schemakenerator.reflection.analyzer.SimpleTypeAnalyzerModule
import io.github.smiley4.schemakenerator.reflection.analyzer.TypeCategoryAnalyzer.Companion.DEFAULT_PRIMITIVE_TYPES
import io.github.smiley4.schemakenerator.reflection.data.EnumConstType
import io.github.smiley4.schemakenerator.serialization.SerializationSteps.addJsonClassDiscriminatorProperty
import io.github.smiley4.schemakenerator.serialization.SerializationSteps.analyzeTypeUsingKotlinxSerialization
import io.github.smiley4.schemakenerator.serialization.SerializationSteps.renameMembers
import io.github.smiley4.schemakenerator.serialization.analyzer.KotlinxSerializationCustomProvider
import io.github.smiley4.schemakenerator.serialization.analyzer.KotlinxSerializationTypeMatcher
import io.github.smiley4.schemakenerator.serialization.analyzer.SerializationTypeAnalyzerModule
import io.github.smiley4.schemakenerator.serialization.analyzer.SimpleSerializationTypeAnalyzerModule
import io.github.smiley4.schemakenerator.serialization.analyzer.fullName
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.RequiredHandling
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.handleCoreAnnotations
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.handleSchemaAnnotations
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.mergePropertyAttributesIntoType
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.withTitle
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchemaData
import io.github.smiley4.schemakenerator.swagger.data.RefType
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generator.SwaggerSchemaGenerationModule
import io.swagger.v3.oas.models.media.Schema
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import kotlin.uuid.ExperimentalUuidApi

/**
 * Function to generate swagger schemas for any given type
 */
typealias GenericSchemaGenerator = (type: InitialTypeData) -> CompiledSwaggerSchemaData

object SchemaGenerator {

    /**
     * A pre-built [GenericSchemaGenerator] using reflection to analyze types and generate the schemas
     * @param config the configuration of the schema generation
     */
    fun reflection(config: ReflectionConfig.() -> Unit = {}): GenericSchemaGenerator {
        val configInstance = ReflectionConfig().apply(config)
        return { type ->
            type
                .collectSubTypes()
                .analyzeTypeUsingReflection {
                    includeGetters = configInstance.includeGetters
                    includeWeakGetters = configInstance.includeWeakGetters
                    includeFunctions = configInstance.includeFunctions
                    includeHidden = configInstance.includeHidden
                    includeStatic = configInstance.includeStatic
                    primitiveTypes = configInstance.primitiveTypes
                    enumConstType = configInstance.enumConstType
                    modules.addAll(configInstance.analyzerModules)
                }
                .addMissingSupertypeSubtypeRelations()
                .handleNameAnnotation()
                .let {
                    if (configInstance.discriminatorProperty != null) it.addDiscriminatorProperty(configInstance.discriminatorProperty!!)
                    else it
                }
                .generateSwaggerSchema {
                    optionals = configInstance.optionals
                    nullables = configInstance.nullables
                    customModules.addAll(configInstance.generationModules)
                }
                .handleCoreAnnotations()
                .handleSchemaAnnotations()
                .let {
                    if (configInstance.title != null) it.withTitle(configInstance.title!!)
                    else it
                }
                .mergePropertyAttributesIntoType()
                .compileReferencingRoot(
                    explicitNullTypes = configInstance.explicitNullTypes,
                    pathType = configInstance.referencePath
                )
        }
    }


    /**
     * The configuration for a pre-built schema generator using reflection for type analysis.
     */
    class ReflectionConfig internal constructor() {

        /**
         * Whether to include getters as members of classes (see [io.github.smiley4.schemakenerator.core.data.MemberKind.GETTER]).
         */
        var includeGetters: Boolean = false


        /**
         * Whether to include weak getters as members of classes (see [io.github.smiley4.schemakenerator.core.data.MemberKind.WEAK_GETTER]).
         */
        var includeWeakGetters: Boolean = false


        /**
         * Whether to include functions as members of classes (see [io.github.smiley4.schemakenerator.core.data.MemberKind.FUNCTION]).
         */
        var includeFunctions: Boolean = false


        /**
         * Whether to include hidden (e.g. private) members
         */
        var includeHidden: Boolean = false


        /**
         * Whether to include static members
         */
        var includeStatic: Boolean = false


        /**
         * The list of types that are considered "primitive types"
         */
        var primitiveTypes: MutableSet<KClass<*>> = DEFAULT_PRIMITIVE_TYPES.toMutableSet()


        /**
         * Whether to use "toString" for enum values or the declared "name"
         */
        var enumConstType: EnumConstType = EnumConstType.NAME


        /**
         * The name of the discriminator property. Set `null` to not include any discriminator property.
         */
        var discriminatorProperty: String? = null


        /**
         * Whether optional properties are treated as "required". An optional parameter is one that has a default value specified.
         */
        var optionals: RequiredHandling = RequiredHandling.REQUIRED


        /**
         * Whether nullable properties are treated as "required"
         */
        var nullables: RequiredHandling = RequiredHandling.NON_REQUIRED


        /**
         * Whether to explicitly include "null" types for nullable properties.
         */
        var explicitNullTypes: Boolean = true


        /**
         * The format of the titles. Set `null` to not include titles in the schemas.
         */
        var title: TitleType? = TitleType.SIMPLE


        /**
         * The format of the reference paths.
         */
        var referencePath: RefType = RefType.OPENAPI_FULL


        /**
         * List of additional/custom [ReflectionTypeAnalyzerModule] to use for analysis.
         */
        var analyzerModules = mutableListOf<ReflectionTypeAnalyzerModule>()


        /**
         * Adds a new [ReflectionTypeAnalyzerModule].
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(module: ReflectionTypeAnalyzerModule) {
            analyzerModules.add(module)
        }


        /**
         * Add a new custom analyzer for types matched by the given matcher.
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(matcher: ReflectionTypeMatcher, provider: ReflectionCustomProvider) {
            analyzerModules.add(SimpleTypeAnalyzerModule(matcher, provider))
        }


        /**
         * Add a custom type overwriting the given type.
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(clazz: KClass<*>, provider: ReflectionCustomProvider) {
            customAnalyzer(
                { _: KType, c: KClass<*> -> c == clazz },
                provider
            )
        }


        /**
         * Add a custom type overwriting the given type.
         * Modules overwrite previous modules when matching the same type.
         */
        inline fun <reified T> customAnalyzer(noinline provider: ReflectionCustomProvider) {
            customAnalyzer(typeOf<T>().classifier!! as KClass<*>, provider)
        }


        /**
         * List of additional/custom [ReflectionTypeAnalyzerModule] to use for schema generation.
         */
        val generationModules = mutableListOf<SwaggerSchemaGenerationModule>()


        /**
         * Add a custom schema generation module.
         */
        fun customGenerator(module: SwaggerSchemaGenerationModule) {
            generationModules.add(module)
        }


        /**
         * Specify the schema for the matching type. Overwrites default schema generation
         */
        fun overwrite(module: SchemaOverwriteModule) {
            analyzerModules.add(module)
            generationModules.add(module)
        }

    }


    /**
     * A pre-built [GenericSchemaGenerator] using reflection to analyze types and generate the schemas
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun kotlinx(json: Json? = null, config: KotlinxSerializationConfig.() -> Unit = {}): GenericSchemaGenerator {
        val configInstance = KotlinxSerializationConfig()
            .apply { if (json != null) useKotlinxConfig(json) }
            .apply(config)
        return { type ->
            type
                .analyzeTypeUsingKotlinxSerialization {
                    serializersModule = configInstance.serializersModule
                    knownNotParameterized = configInstance.knownNotParameterized
                    customModules.addAll(configInstance.analyzerModules)
                }
                .addJsonClassDiscriminatorProperty()
                .handleNameAnnotation()
                .let {
                    if(configInstance.namingStrategy != null) it.renameMembers(configInstance.namingStrategy!!)
                    else it
                }
                .generateSwaggerSchema {
                    optionals = configInstance.optionals
                    nullables = configInstance.nullables
                    customModules.addAll(configInstance.generationModules)
                }
                .handleCoreAnnotations()
                .handleSchemaAnnotations()
                .let {
                    if (configInstance.title != null) it.withTitle(configInstance.title!!)
                    else it
                }
                .mergePropertyAttributesIntoType()
                .compileReferencingRoot(
                    explicitNullTypes = configInstance.explicitNullTypes,
                    pathType = configInstance.referencePath
                )
        }
    }


    /**
     * The configuration for a pre-built schema generator using kotlinx-serialization for type analysis.
     */
    @OptIn(ExperimentalSerializationApi::class)
    class KotlinxSerializationConfig internal constructor() {

        /**
         * kotlinx serializers module from `Json { }.serializersModule` for support of contextual serializers
         */
        var serializersModule: SerializersModule? = null


        /**
         * The types that are guaranteed to not have type parameters.
         * This helps the type processing step to determine whether two types are truly the same and may fix issues encountered with types.
         */
        var knownNotParameterized = mutableSetOf<String>()


        /**
         * Mark the type with the given full/qualified name as "not parameterized", i.e. as not having any generic type parameters.
         * This helps the type processing step to determine whether two types are truly the same and may fix issues encountered with types.
         */
        fun markNotParameterized(name: String) {
            knownNotParameterized.add(name)
        }


        /**
         * Mark the given type as "not parameterized", i.e as not having any generic type parameters.
         * This helps the type processing step to determine whether two types are truly the same and may fix issues encountered with types.
         */
        fun markNotParameterized(type: KType) {
            val clazz = type.classifier!! as KClass<*>
            markNotParameterized(clazz.qualifiedName ?: clazz.java.name)
        }


        /**
         * Mark the given type as "not parameterized", i.e as not having any generic type parameters.
         * This helps the type processing step to determine whether two types are truly the same.
         */
        inline fun <reified T> markNotParameterized() {
            val clazz = typeOf<T>().classifier!! as KClass<*>
            markNotParameterized(clazz.qualifiedName ?: clazz.java.name)
        }


        /**
         * Whether optional properties are treated as "required". An optional parameter is one that has a default value specified.
         */
        var optionals: RequiredHandling = RequiredHandling.REQUIRED


        /**
         * Whether nullable properties are treated as "required"
         */
        var nullables: RequiredHandling = RequiredHandling.NON_REQUIRED


        /**
         * Whether to explicitly include "null" types for nullable properties.
         */
        var explicitNullTypes: Boolean = true


        /**
         * The naming strategy used to rename members/properties. Set `null` to not do any additional renaming.
         */
        var namingStrategy: JsonNamingStrategy? = null

        /**
         * The format of the titles. Set `null` to not include titles in the schemas.
         */
        var title: TitleType? = TitleType.SIMPLE


        /**
         * The format of the reference paths.
         */
        var referencePath: RefType = RefType.OPENAPI_FULL


        /**
         * List of additional/custom [SerializationTypeAnalyzerModule] to use for analysis.
         */
        var analyzerModules = mutableListOf<SerializationTypeAnalyzerModule>()


        /**
         * Adds a new [SerializationTypeAnalyzerModule].
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(module: SerializationTypeAnalyzerModule) {
            analyzerModules.add(module)
        }


        /**
         * Add a new custom type for types matched by the given matcher.
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(matcher: KotlinxSerializationTypeMatcher, provider: KotlinxSerializationCustomProvider) {
            customAnalyzer(SimpleSerializationTypeAnalyzerModule(matcher, provider))
        }


        /**
         * Add a new custom type for types matched by the given serial name.
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(serializerName: String, provider: KotlinxSerializationCustomProvider) {
            customAnalyzer(
                { descriptor: SerialDescriptor -> descriptor.fullName() == serializerName },
                provider
            )
        }


        /**
         * Add a custom type overwriting the given type.
         * Modules overwrite previous modules when matching the same type.
         */
        fun customAnalyzer(type: KClass<*>, provider: KotlinxSerializationCustomProvider) {
            customAnalyzer(
                { descriptor: SerialDescriptor -> descriptor.fullName() == (type.qualifiedName ?: type.java.name) },
                provider
            )
        }


        /**
         * Add a custom type overwriting the given type.
         * Modules overwrite previous modules when matching the same type.
         */
        inline fun <reified T> customAnalyzer(noinline provider: KotlinxSerializationCustomProvider) {
            customAnalyzer(typeOf<T>().classifier!! as KClass<*>, provider)
        }


        /**
         * List of additional/custom [ReflectionTypeAnalyzerModule] to use for schema generation.
         */
        val generationModules = mutableListOf<SwaggerSchemaGenerationModule>()


        /**
         * Add a custom schema generation module.
         */
        fun customGenerator(module: SwaggerSchemaGenerationModule) {
            generationModules.add(module)
        }


        /**
         * Specify the schema for the matching type. Overwrites default schema generation
         */
        fun overwrite(module: SchemaOverwriteModule) {
            analyzerModules.add(module)
            generationModules.add(module)
        }


        /**
         * Initialize this schema generator config using the given kotlinx json serializer and match its behavior as close as possible.
         * @param json the kotlinx json serializer
         */
        fun useKotlinxConfig(json: Json) {
            serializersModule = json.serializersModule
            optionals = if (json.configuration.encodeDefaults) RequiredHandling.REQUIRED else RequiredHandling.NON_REQUIRED
            nullables = if (json.configuration.explicitNulls) RequiredHandling.REQUIRED else RequiredHandling.NON_REQUIRED
            namingStrategy = json.configuration.namingStrategy
        }

    }

    object TypeOverwrites {

        /**
         * Custom analysis and schema generation module for handling [java.util.UUID].
         * Generates a swagger schema with type = "string" and format = "uuid".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class JavaUuid : SchemaOverwriteModule(
            identifier = java.util.UUID::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "uuid"
                }
            },
        )


        /**
         * Custom analysis and schema generation module for handling [kotlin.uuid.Uuid].
         * Generates a swagger schema with type = "string" and format = "uuid".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class KotlinUuid : SchemaOverwriteModule(
            identifier = kotlin.uuid.Uuid::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "uuid"
                }
            },
        )


        /**
         * Custom analysis and schema generation module for handling [java.io.File].
         * Generates a swagger schema with type = "string" and format = "binary".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class File : SchemaOverwriteModule(
            identifier = java.io.File::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "binary"
                }
            },
        )


        /**
         * Custom analysis and schema generation module for handling [java.time.Instant].
         * Generates a swagger schema with type = "string" and format = "date-time".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class Instant : SchemaOverwriteModule(
            identifier = java.time.Instant::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "date-time"
                }
            },
        )


        /**
         * Custom analysis and schema generation module for handling [java.time.LocalDateTime].
         * Generates a swagger schema with type = "string" and format = "date-time".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class LocalDateTime : SchemaOverwriteModule(
            identifier = java.time.LocalDateTime::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "date-time"
                }
            },
        )


        /**
         * Custom analysis and schema generation module for handling [java.time.LocalDate].
         * Generates a swagger schema with type = "string" and format = "date".
         * Can be registered in the config for [SchemaGenerator.reflection] or [SchemaGenerator.kotlinx]
         */
        class LocalDate : SchemaOverwriteModule(
            identifier = java.time.LocalDate::class.qualifiedName!!,
            schema = {
                Schema<Any>().also {
                    it.types = setOf("string")
                    it.format = "date"
                }
            },
        )

    }

}
