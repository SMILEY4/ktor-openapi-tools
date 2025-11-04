# Customizing Automatically Generated Schemas

Automatic schema generation can be customized to handle specific types differently, apply custom naming strategies, or control schema
structure. Customization is available at multiple levels: complete pipeline configuration, pre-built generator options, custom type
analyzers, and custom schema generators.

## Building a Custom Schema Generation Pipeline

The most flexible approach is building a custom pipeline directly using schema-kenerator. This provides complete control over each step of
the generation process.

```kotlin
install(OpenApi) {
    schemas {
        generator = { type ->
            type
                .analyzeTypeUsingKotlinxSerialization()
                .generateSwaggerSchema {
                    nullables = RequiredHandling.NON_REQUIRED
                    optionals = RequiredHandling.REQUIRED
                }
                .withTitle(TitleType.SIMPLE)
                .compileReferencingRoot(
                    explicitNullTypes = false
                )
        }
    }
}
```

This basic pipeline:

1. Analyzes types using kotlinx.serialization
2. Generates independent Swagger schemas for each type
3. Adds titles
4. Merges schemas into final schema with references

??? info "More Information"

    The schema-kenerator library provides extensive documentation on pipeline configuration and complete customization options.

    [:octicons-arrow-right-24: schema-kenerator Documentation](https://smiley4.github.io/schema-kenerator/latest/)

## Configuring Pre-Build Generators

The plugin provides pre-configured generators with simplified configuration options. These handle the pipeline internally while exposing
commonly needed settings.

### Basic Reflection Generator Configuration

??? info "API Reference"

    The full list of available configuration options can be found in the API reference:

    [:octicons-arrow-right-24: API Reference](../../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-generator/-reflection-config/index.html)

**Property inclusion:**

Control which class members are included in the schema. By default, only constructor parameters are included. Enable `includeGetters` to
include getter methods as properties.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            includeGetters = false
            includeWeakGetters = false
            includeFunctions = false
            includeHidden = false
            includeStatic = false
        }
    }
}
```

**Required field handling**

Determine how optional and nullable properties affect the required array in schemas. `RequiredHandling.REQUIRED` includes the property in
the
required list, `RequiredHandling.NON_REQUIRED` makes it optional.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            optionals = RequiredHandling.REQUIRED
            nullables = RequiredHandling.NON_REQUIRED
        }
    }
}
```

**Polymorphism**

Configures a discriminator property to schemas with subtypes. The discriminator helps distinguish between different implementations of a
base type.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            discriminatorProperty = "type"
        }
    }
}
```

**Schema structure**

Control whether nullable types explicitly include "null" as a type, what format titles should have, and the reference path format.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            explicitNullTypes = true
            title = TitleType.SIMPLE
            referencePath = RefType.OPENAPI_FULL
        }
    }
}
```

Controls what types should be treated as primitives (results in simplified type analysis and schema generation) and what constants to use
for enum types.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            primitiveTypes = DEFAULT_PRIMITIVE_TYPES.toMutableSet()
            enumConstType = EnumConstType.NAME
        }
    }
}
```

### Basic Kotlinx.Serialization Generator Configuration

??? info "API Reference"

    The full list of available configuration options can be found in the API reference:

    [:octicons-arrow-right-24: API Reference](../../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-schema-generator/-kotlinx-serialization-config/index.html)

**Serialization module**

Provide the serializers module from your Json configuration to support contextual serializers.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx {
            serializersModule = mySerializersModule
        }
    }
}
```

**Naming strategy**

Apply a naming strategy (snake_case, camelCase, etc.) to property names in schemas. Should match your Json configuration.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx {
            namingStrategy = JsonNamingStrategy.SnakeCase
        }
    }
}
```

**Required field handling**

Determine how optional and nullable properties affect the required array in schemas. `RequiredHandling.REQUIRED` includes the property in
the
required list, `RequiredHandling.NON_REQUIRED` makes it optional.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx {
            optionals = RequiredHandling.REQUIRED
            nullables = RequiredHandling.NON_REQUIRED
        }
    }
}
```

**Schema Structure**

Controls the basic structure of schemas.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx {
            explicitNullTypes = true
            title = TitleType.SIMPLE
            referencePath = RefType.OPENAPI_FULL
        }
    }
}
```

### Configuring Kotlinx.Serialization Generator From Json Configuration

The kotlinx.serialization generator can automatically match a given Json configuration:

```kotlin
val json = Json {
    encodeDefaults = true
    explicitNulls = false
    namingStrategy = JsonNamingStrategy.SnakeCase
}

install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx(json) {
            // Additional configuration
        }
    }
}
```

This automatically sets `optionals`, `nullables`, `namingStrategy`, and `serializersModule` based on the Json configuration, ensuring
schemas match actual serialization behavior.

## Overwriting types

Type overwrites provide a simple way to replace schema generation for specific types with an own fixed schema.
Internally, they combine a custom type analyzer and generator.

### Implementing a Custom Type Overwrite

```kotlin
object EmailAddressOverwrite : SchemaOverwriteModule(
    identifier = "com.example.EmailAddress", // (1)!
    schema = {
        Schema<Any>().also { // (2)!
            it.types = setOf("string")
            it.format = "email"
            it.pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
        }
    }
)
```

1. Replace types with (qualified) name matching `com.example.EmailAddress`.
2. Replace default schema with own provided schema.

The overwrite specifies:

- *Identifier:* The fully qualified type name to match. When using kotlinx.serialization and custom serializers, this name must match the
  serial descriptor name.
- *Schema:* A lambda that produces the replacement schema

### Registering a Custom Type Overwrite

=== "Reflection Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection() {
                overwrite(EmailAddressOverwrite)
            }
        }
    }
    ```

=== "Kotlinx.Serialization Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx() {
                overwrite(EmailAddressOverwrite)
            }
        }
    }
    ```

Type overwrites work the same with both generators.

This ensures that `EmailAddress` types always generate a string schema with email format and validation pattern.

### Pre-Built Type Overwrites

The plugin provides pre-built overwrites for common types that need special handling:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            overwrite(SchemaGenerator.TypeOverwrites.JavaUuid())
            overwrite(SchemaGenerator.TypeOverwrites.LocalDateTime())
            overwrite(SchemaGenerator.TypeOverwrites.File())
            // ...
        }
    }
}
```

**Available pre-built overwrites:**

| Type                      | Overwrite                        | Schema Type | Schema Format |
|---------------------------|----------------------------------|-------------|---------------|
| `java.util.UUID`          | `TypeOverwrites.JavaUuid()`      | string      | uuid          |
| `kotlin.uuid.Uuid`        | `TypeOverwrites.KotlinUuid()`    | string      | uuid          |
| `java.io.File`            | `TypeOverwrites.File()`          | string      | binary        |
| `java.time.Instant`       | `TypeOverwrites.Instant()`       | string      | date-time     |
| `java.time.LocalDateTime` | `TypeOverwrites.LocalDateTime()` | string      | date-time     |
| `java.time.LocalDate`     | `TypeOverwrites.LocalDate()`     | string      | date          |

These overwrites ensure standard library types generate schemas with appropriate formats that match common API patterns.

## Creating Custom Analyzers for types

Analyzers examine Kotlin types and produce type data describing its structure.. Custom analyzers can handle types that don't work correctly with default
analysis or require special treatment.

A simplified version of an analyzer can be registered for a given type:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {

            customAnalyzer<EmailAddress> { typeId -> // (1)!
                TypeData( // (2)!
                    id = typeId, // (3)!
                    descriptiveName = TypeName( //(4)!
                        full = EmailAddress::class.qualifiedName!!,
                        short = EmailAddress::class.simpleName!!,
                    ),
                    identifyingName = TypeName( //(5)!
                        full = String::class.qualifiedName!!,
                        short = String::class.simpleName!!,
                    ),
                    annotations = mutableListOf(
                        AnnotationData( //(6)!
                            name = Format::class.qualifiedName!!,
                            values = mutableMapOf("format" to "email")
                        )
                    )
                )
            }

        }
    }
}
```

1. Register a new (simplified) analyzer for type `EmailAddress`. Different registration functions with different parameters exist.
2. Build a new `TypeData` for the specified type. `TypeData` contains all information used to generate a final schema.
3. Every `TypeData` has a unique id. Always use the provided one for custom analyzer.
4. Define a descriptive name. This does not have an impact on the final schema except for titles and reference paths.
5. Define an identifying name. This name directly influences the type of the final schema.
6. Add the schema-kenerator `@Format` annotation to specify the `email` format.

This results in the following schema for `EmailAddress`:

```json
{
  "type" : "string",
  "format" : "email",
  "title" : "EmailAddress"
}
```

More complex and powerful analyzers can be created by overwriting and registering a `ReflectionTypeAnalyzerModule`:

```kotlin
class ProxyCustomAnalyzer : ReflectionTypeAnalyzerModule { //(1)!

    override fun applies(type: KType, clazz: KClass<*>): Boolean {
        return type == typeOf<EmailAddress>()  //(2)!
    }

    override fun preAnalyze(context: ReflectionTypeAnalyzerModule.Context): MinimalTypeData {
        return MinimalTypeData(  //(3)!
            identifyingName = TypeName(
                full = context.clazz.qualifiedName ?: context.clazz.java.name,
                short = context.clazz.simpleName ?: context.clazz.java.name
            ),
            descriptiveName = TypeName(
                full = context.clazz.qualifiedName ?: context.clazz.java.name,
                short = context.clazz.simpleName ?: context.clazz.java.name
            ),
            typeParameters = emptyList()
        )
    }

    override fun analyze(
        context: ReflectionTypeAnalyzerModule.Context,
        minimalTypeData: MinimalTypeData
    ): WrappedTypeData {
        println("Analyzing ${minimalTypeData.descriptiveName.full}!")
        return context.analyze(typeOf<String>(), String::class)  //(4)!
    }

}

generator = SchemaGenerator.reflection {
    customAnalyzer(ProxyCustomAnalyzer())  //(5)!
}
```

1. Implement `ReflectionTypeAnalyzerModule` or `SerializationTypeAnalyzerModule`.
2. Determine for which types this analyzer applies to - only `EmailAddress` in this case.
3. Provide some basic information about the type in the first step.
4. Full analysis step. Context provides complete analysis data and functionality. In this case, a `String` is analyzed instead of `EmailAddress`.
5. Register the custom analyzer. Registration works the same for reflection and kotlinx.serialization. 

Custom analyzers are evaluated in registration order. When a type matches multiple analyzers, the last registered analyzer is used.

Full type analysis capabilities can be accessed via the provided `Context` object, e.g. allowing for property types to be analyzed by the default analyzer using `context.analyze(...)`.

## Creating Custom Schema Generators for types

Schema generators convert type data (produced by analyzers) into OpenAPI schemas. Custom generators can produce schemas with specific
structures, validation rules, or formats.

### Implementing a Custom Generator

```kotlin
class EmailAddressGenerator : SwaggerSchemaGenerationModule { //(1)!

    override fun applies(typeData: TypeData): Boolean {
        return typeData.descriptiveName.full == EmailAddress::class.qualifiedName!! //(2)!
    }

    override fun generate(context: SwaggerSchemaGenerationModule.Context): Schema<*> {
        return Schema<Any>().also { //(3)!
            it.types = setOf("string")
            it.format = "email"
            it.pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
        }
    }

}


install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection() {
            customGenerator(EmailAddressGenerator()) //(4)!
        }
    }
}
```

1. Implement `SwaggerSchemaGenerationModule`.
2. Determine for which types this generator applies to - only `EmailAddress` in this case.
3. Create the schema for the given type. The context object provides complete analysis data and functionality.
4. Register the custom generator. Registration works the same for reflection and kotlinx.serialization.


Multiple generators can be registered. They are evaluated in order until one returns a non-null schema.

Full type analysis capabilities can be accessed via the provided `Context` object, e.g. allowing for property schemas to be generated by the default generator using `context.generate(...)`.
