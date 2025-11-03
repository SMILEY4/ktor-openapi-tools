# Providing OpenAPI Specification

The OpenAPI plugin generates specifications conforming to the OpenAPI 3.1.0 specification. Generated specifications are complete, self-contained documents that describe the API's structure, operations, data models, and security requirements.

Specifications are generated during application initialization based on registered routes and plugin configuration. The generation process collects route documentation, generates schemas for referenced types, encodes examples, and assembles all components into a valid OpenAPI document.

Available formats are JSON and YAML.

## Accessing the Specification

### HTTP Route

The standard method for providing specifications is through HTTP routes:

```kotlin
import io.github.smiley4.ktoropenapi.openApi

routing {
    route("api.json") {
        openApi()
    }
}
```

The `openAPI()` function creates a new route that returns the generated specification.

### Programmatic Access

Generated specifications can be retrieved programmatically without HTTP requests.

```kotlin
// Retrieve the (default) specification
val spec: String = OpenApiPlugin.getOpenApiSpec(
    OpenApiPluginConfig.DEFAULT_SPEC_ID
)

// Retrieve the format of the (default) specification
val format: OutputFormat = OpenApiPlugin.getOpenApiSpecFormat(
    OpenApiPluginConfig.DEFAULT_SPEC_ID
)
```

Specifications can be regenerated programmatically at any time.

```kotlin
// Regenerate the (default) specification
OpenApiPlugin.regenerateOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID) 
```

This discard a previously created specification, Collect all routes and information again and generates a new specification.


## Output Format

By default, OpenAPI specifications are generated as json. This can be configured in the plugin configuration

```kotlin
install(OpenApi) {
    outputFormat = OutputFormat.YAML
}
```