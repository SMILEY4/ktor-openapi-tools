# Example Encoding

Example objects are automatically serialized (as json) and added to the OpenAPI specification.
The encoding of example values can be customized in the plugin configuration.
If no encoder is specified, the example value will be encoded by swagger.

## Pre-Defined Example Encoders

=== "Swagger"
    Explicitly use the internal swagger example encoder. 

    ```kotlin
    install(OpenApi) {
        examples {
            encoder = ExampleEncoder.internal()
        }
    }
    ```

    ??? info "More Information"

        [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-example-encoder/internal.html)

=== "Kotlinx.Serialization"
    Use [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) to encode example values.

    ```kotlin
    install(OpenApi) {
        examples {
            encoder = ExampleEncoder.kotlinx()
        }
    }
    ```

    ??? info "More Information"

        [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-example-encoder/kotlinx.html)


## Custom Example Encoder

The example encoder can be completely replaced by an own implementation.

```kotlin
install(OpenApi) {
    examples {
        encoder { type, example -> //(1)!
            TOOD() //(2)!
        }
    }
}
```

1. Input of the example encoding function is a `io.github.smiley4.ktoropenapi.config.TypeDescriptor` with information about the type/schema and the actual value of the example.
2. Encode/Transform the example value and return the result.

???+ example "Custom "toString()" Example Encoder"

    ```kotlin
    install(OpenApi) {
        examples {
            encoder { type, example ->
                example.toString() //(1)!
            }
        }
    }
    ```
    
    1. Always encode and embed the example value as a raw string.