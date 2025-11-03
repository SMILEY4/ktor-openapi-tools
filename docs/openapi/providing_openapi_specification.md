# Providing OpenAPI Specification

The OpenAPI plugin generates specifications conforming to the OpenAPI 3.1.0 specification. Generated specifications are complete, self-contained
documents that describe the API's structure, operations, data models, and security requirements.

Specifications are generated during application initialization based on registered routes and plugin configuration. The generation process
collects route documentation, generates schemas for referenced types, encodes examples, and assembles all components into a valid OpenAPI document.




## Accessing the Specification

### HTTP Route

The standard method for providing specifications is through HTTP routes:

```kotlin
import io.github.smiley4.ktoropenapi.openApi

routing {
    route("api.json") { // (1)!
        openApi() // (2)!
    }
}
```

1. Define a new normal Ktor route for `/api.json`.
2. Serve the (default) OpenAPI specification at this location.

The `openAPI()` function creates a new route that returns the generated specification.

### Programmatic Access

Generated specifications can be retrieved programmatically without HTTP requests.

```kotlin
val spec: String = OpenApiPlugin.getOpenApiSpec( // (1)!
    OpenApiPluginConfig.DEFAULT_SPEC_ID
)

val format: OutputFormat = OpenApiPlugin.getOpenApiSpecFormat( // (2)!
    OpenApiPluginConfig.DEFAULT_SPEC_ID
)
```

1. Retrieve the (default) specification.
2. Retrieve the format of the (default) specification (either JSON or YAML).

Specifications can be regenerated programmatically at any time.

```kotlin
OpenApiPlugin.regenerateOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID) 
```

This discards a previously created specification, collects all routes and information again and generates a new specification.


## Output Format

By default, OpenAPI specifications are generated as json. This can be configured in the plugin configuration

```kotlin
install(OpenApi) {
    outputFormat = OutputFormat.YAML // (1)!
}
```

1. Available options are `OutputFormat.JSON` and `OutputFormat.YAML`.