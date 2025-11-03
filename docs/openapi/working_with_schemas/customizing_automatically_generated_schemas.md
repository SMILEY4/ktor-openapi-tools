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
4. Compiles final schemas with references

More information can be found in the schema-kenerator documentation.

## Configuring Pre-Build Generators

The plugin provides pre-configured generators with simplified configuration options. These handle the pipeline internally while exposing
commonly needed settings.

### Basic Reflection Generator Configuration

**Property inclusion:**

Control which class members are included in the schema. By default, only constructor parameters are included. Enable includeGetters to
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

Determine how optional and nullable properties affect the required array in schemas. RequiredHandling.REQUIRED includes the property in the
required list, RequiredHandling.NON_REQUIRED makes it optional.

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

Determine how optional and nullable properties affect the required array in schemas. RequiredHandling.REQUIRED includes the property in the
required list, RequiredHandling.NON_REQUIRED makes it optional.

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

The kotlinx.serialization generator can automatically match Json configuration:

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

## Creating Custom Analyzers for types

Analyzers extract structural information from Kotlin types. Custom analyzers can handle types that don't work correctly with default
analysis or require special treatment.

### Implementing a Custom Analyzer

Analyzers examine a type and produce type data describing its structure:

```kotlin
todo
```

The analyzer receives a KType (for reflection) or SerialDescriptor (for kotlinx.serialization) and returns type data or null to use default
behavior.

### Registering a Custom Analyzer

=== "Reflection Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection() {
            }
        }
    }
    ```

=== "Kotlinx.Serialization Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx() {
            }
        }
    }

Custom analyzers are evaluated in registration order. When a type matches multiple analyzers, the last registered analyzer is used.

## Creating Custom Schema Generators for types

Schema generators convert type data (produced by analyzers) into OpenAPI schemas. Custom generators can produce schemas with specific
structures, validation rules, or formats.

### Implementing a Custom Analyzer

```kotlin
todo
```

The generator receives type data and returns a schema or null for default behavior.

### Registering a Custom Generator

=== "Reflection Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection() {
            }
        }
    }
    ```

=== "Kotlinx.Serialization Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx() {
            }
        }
    }

Multiple generators can be registered. They are evaluated in order until one returns a non-null schema.

## Overwriting types

Type overwrites provide a simplified way to replace schema generation for specific types with a fixed schema. They combine a custom analyzer
and generator internally.

Type Overwrites behave the same for reflection and kotlinx.serialization generators.

### Implementing a Custom Type Overwrite

```kotlin
class EmailAddressOverwrite : SchemaOverwriteModule(
    identifier = "com.example.EmailAddress",
    schema = {
        Schema<Any>().also {
            it.types = setOf("string")
            it.format = "email"
            it.pattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"
        }
    }
)
```

This ensures that `EmailAddress` types always generate a string schema with email format and validation pattern.

The overwrite specifies:

- *Identifier:* The fully qualified type name to match. When using kotlinx.serialization, this name must match the serial descriptor.
- *Schema:* A lambda that produces the replacement schema

### Registering a Custom Type Overwrite

=== "Reflection Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.reflection() {
                overwrite(MyTypeOverwrite())
            }
        }
    }
    ```

=== "Kotlinx.Serialization Generator"

    ```kotlin
    install(OpenApi) {
        schemas {
            generator = SchemaGenerator.kotlinx() {
                overwrite(MyTypeOverwrite())
            }
        }
    }

Type overwrites work the same with both generators because they replace the entire analysis and generation process for the matched type.

### Pre-Built Type Overwrites

The plugin provides pre-built overwrites for common types that need special handling:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            // Java UUID
            overwrite(SchemaGenerator.TypeOverwrites.JavaUuid())

            // Kotlin UUID
            overwrite(SchemaGenerator.TypeOverwrites.KotlinUuid())

            // File
            overwrite(SchemaGenerator.TypeOverwrites.File())

            // Java time types
            overwrite(SchemaGenerator.TypeOverwrites.Instant())
            overwrite(SchemaGenerator.TypeOverwrites.LocalDateTime())
            overwrite(SchemaGenerator.TypeOverwrites.LocalDate())
        }
    }
}
```

**Available pre-built overwrites:**

| Type             | Overwrite                                   | Schema Type | Schema Format |
|------------------|---------------------------------------------|-------------|---------------|
| `java.util.UUID` | `SchemaGenerator.TypeOverwrites.JavaUuid()` | string      | uuid          |
| todo             | todo                                        | todo        | todo          |

These overwrites ensure standard library types generate schemas with appropriate formats that match common API patterns.