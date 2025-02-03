# Configuration OpenAPI (Shared) Examples

## Customizing Example Encoding
The encoding of example values can be customized with the encoder config.
If no encoder is specified, the example value will be encoded by swagger.

```kotlin
install(OpenApi) {
    examples {
        encoder { type, example ->
            // always encoding example as a string
            example.toString()
        }
    }
}
```

## Shared Examples

Global or shared examples can be defined in the examples section of the plugin config and then referenced by route documentation.
Shared examples are placed in the components/examples section of the final OpenAPI spec.

### Defining Shared Examples

```kotlin
install(OpenApi) {
    examples {
        example("Shared A") {
            description = "first shared example"
            value = MyExampleClass(
                someValue = "shared a"
            )
        }

        example("Shared B") {
            description = "second shared example"
            value = MyExampleClass(
                someValue = "shared b"
            )
        }
    }
}
```

### Referencing Shared Examples in Routes

```kotlin
body<MyExampleClass> {
    // reference two shared examples specified in the plugin configuration
    exampleRef("Example 1", "Shared A")
    exampleRef("Example 2", "Shared B")
}
```
