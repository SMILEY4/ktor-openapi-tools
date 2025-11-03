# Configuring Example Encoding

Example encoding determines how Kotlin objects used as example values are converted to a format suitable for the OpenAPI specification. The encoding process transforms Kotlin objects into JSON that can be embedded in the specification.

## How Example Encoding Works

When examples are defined using Kotlin objects, they must be encoded for inclusion in the OpenAPI specification:

```kotlin
body<User>() {
    example("Example User") {
        value = User(id = "123", name = "John") // Kotlin object
    }
}
```

The encoding process roughly goes as following:
1. The example value (Kotlin object) is passed to the configured encoder
2. The encoder serializes the object to a suitable format (typically JSON)
3. The encoded result is embedded in the OpenAPI specification

The encoder configuration determines how this serialization occurs, allowing it to match the application's actual serialization behavior.

## Example Encoding Configuration

The example encoder is configured in the plugin's examples block:

```kotlin
install(OpenApi) {
    examples {
        encoder = ExampleEncoder.internal() // or other encoder
    }
}
```

The plugin provides three built-in encoding options: internal encoding using Swagger's library, kotlinx.serialization-based encoding, and custom encoding logic.

### Internal Encoding

The internal encoder uses the Swagger library's built-in serialization:

```kotlin
install(OpenApi) {
    examples {
        encoder = ExampleEncoder.internal()
    }
}
```

This is the default encoder and requires no additional configuration. It usually uses Jackson internally to convert example values to JSON.

### Kotlinx.Serialization Encoding

The kotlinx.serialization encoder uses kotlinx.serialization to encode examples:

```kotlin
install(OpenApi) {
    examples {
        encoder = ExampleEncoder.kotlinx()
    }
}
```

This encoder ensures examples are serialized exactly as the application serializes actual request and response data when using kotlinx.serialization.

A kotlinx.serialization Json instance can be provided to match the application's serialization configuration:

```kotlin
val json = Json {
    prettyPrint = true
    encodeDefaults = true
    explicitNulls = false
    namingStrategy = JsonNamingStrategy.SnakeCase
}

install(OpenApi) {
    examples {
        encoder = ExampleEncoder.kotlinx(json)
    }
}
```

This ensures examples are encoded with the same settings used for actual API responses:

### Custom Encoding

Custom encoding logic can be implemented for complete control over example serialization:

```kotlin
install(OpenApi) {
    examples {
        encoder { type, example ->
            // Custom encoding logic
            when {
                // Handle specific types specially
                type is KTypeDescriptor && type.type == typeOf<CustomEncoderData>() -> {
                    // Extract and return just the wrapped value
                    (example as CustomEncoderData).number
                }
                
                // Convert certain types to strings
                example is SpecialType -> {
                    example.toString()
                }
                
                // Fall back to default encoding for other types
                else -> example
            }
        }
    }
}
```

The encoder function receives:

- `type`: Type containing information about the example's type
- `example`: The actual example value

The function should return the encoded/transformed example value as a Json string or the original example to use default encoding.