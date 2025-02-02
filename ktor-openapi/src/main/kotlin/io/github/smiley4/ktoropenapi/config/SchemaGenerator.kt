package io.github.smiley4.ktoropenapi.config

import io.github.smiley4.schemakenerator.core.addDiscriminatorProperty
import io.github.smiley4.schemakenerator.core.addMissingSupertypeSubtypeRelations
import io.github.smiley4.schemakenerator.core.data.InputType
import io.github.smiley4.schemakenerator.core.handleNameAnnotation
import io.github.smiley4.schemakenerator.reflection.analyseTypeUsingReflection
import io.github.smiley4.schemakenerator.reflection.analyzer.TypeCategoryAnalyzer.Companion.DEFAULT_PRIMITIVE_TYPES
import io.github.smiley4.schemakenerator.reflection.collectSubTypes
import io.github.smiley4.schemakenerator.reflection.data.EnumConstType
import io.github.smiley4.schemakenerator.serialization.addJsonClassDiscriminatorProperty
import io.github.smiley4.schemakenerator.serialization.analyzeTypeUsingKotlinxSerialization
import io.github.smiley4.schemakenerator.swagger.RequiredHandling
import io.github.smiley4.schemakenerator.swagger.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.data.RefType
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.github.smiley4.schemakenerator.swagger.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.handleCoreAnnotations
import io.github.smiley4.schemakenerator.swagger.handleSchemaAnnotations
import io.github.smiley4.schemakenerator.swagger.mergePropertyAttributesIntoType
import io.github.smiley4.schemakenerator.swagger.withTitle
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

/**
 * Function to generate swagger schemas for any given type
 */
typealias GenericSchemaGenerator = (type: InputType) -> CompiledSwaggerSchema

object SchemaGenerator {

    /**
     * A pre-built [GenericSchemaGenerator] using reflection to analyze types and generate the schemas
     */
    fun reflection(config: ReflectionConfig.() -> Unit = {}): GenericSchemaGenerator {
        val configInstance = ReflectionConfig().apply(config)
        return { type ->
            type
                .collectSubTypes()
                .analyseTypeUsingReflection {
                    includeGetters = configInstance.includeGetters
                    includeWeakGetters = configInstance.includeWeakGetters
                    includeFunctions = configInstance.includeFunctions
                    includeHidden = configInstance.includeHidden
                    includeStatic = configInstance.includeStatic
                    primitiveTypes = configInstance.primitiveTypes
                    enumConstType = configInstance.enumConstType
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

    class ReflectionConfig {

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

    }


    /**
     * A pre-built [GenericSchemaGenerator] using reflection to analyze types and generate the schemas
     */
    fun kotlinx(json: Json? = null, config: KotlinxSerializationConfig.() -> Unit = {}): GenericSchemaGenerator {
        val configInstance = KotlinxSerializationConfig()
            .apply { if(json != null) useKotlinxConfig(json) }
            .apply(config)
        return { type ->
            type
                .analyzeTypeUsingKotlinxSerialization {
                    serializersModule = configInstance.serializersModule
                    knownNotParameterized = configInstance.knownNotParameterized
                }
                .addJsonClassDiscriminatorProperty()
                .handleNameAnnotation()
                .generateSwaggerSchema {
                    optionals = configInstance.optionals
                    nullables = configInstance.nullables
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

    class KotlinxSerializationConfig {

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
         * The format of the titles. Set `null` to not include titles in the schemas.
         */
        var title: TitleType? = TitleType.SIMPLE

        /**
         * The format of the reference paths.
         */
        var referencePath: RefType = RefType.OPENAPI_FULL


        /**
         * Initialize this schema generator config using the given kotlinx json serializer and match its behavior as close as possible.
         * @param json the kotlinx json serializer
         */
        fun useKotlinxConfig(json: Json) {
            serializersModule = json.serializersModule
            optionals = if(json.configuration.encodeDefaults) RequiredHandling.REQUIRED else RequiredHandling.NON_REQUIRED
            nullables = if(json.configuration.explicitNulls) RequiredHandling.REQUIRED else RequiredHandling.NON_REQUIRED
        }

    }

}
