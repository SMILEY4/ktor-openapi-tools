# Plugin Configuration

The OpenAPI plugin is configured during installation. All configuration is optional and the plugin works with default settings,
though providing basic API information is recommended.

??? info "API Reference"

    The full list of available configuration options can be found in the API reference:

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-open-api-plugin-config/index.html)




## Installation

The plugin is installed and configured using the standard Ktor plugin mechanism:

```kotlin
import io.github.smiley4.ktoropenapi.OpenApi

install(OpenApi) {
    // Configuration goes here
}
```




## Info Section

The info section defines basic metadata about the API.

```kotlin
install(OpenApi) {
    info {
        title = "Example API"
        version = "latest"
        description = "An example api."
        termsOfService = "example.com"
        contact {
            name = "Mr. Example"
            url = "example.com"
            email = "example@example.com"
        }
        license {
            name = "Example License"
            url = "example.com"
            identifier = "Apache-2.0"
        }
    }
}
```




## Server Configuration

The servers section defines base URLs where the API is available. Multiple servers can be specified and server variables are supported for dynamic server configuration. Documentation UIs like Swagger UI use these URLs for "Try it out" features.

```kotlin
install(OpenApi) {
    server {
        url = "http://localhost:8080"
        description = "Development server"
    }
    server {
        url = "https://staging.example.com"
        description = "Staging environment"
    }
    server {
        url = "https://api.example.com"
        description = "Production server"
    }
}
```

Using server variables:

```kotlin
server {
    url = "https://{environment}.example.com" // (1)!
    variable("environment") {  // (2)!
        description = "Environment name"
        default = "api"
        enum = setOf("api", "staging", "dev")
    }
}
```

1. The URL the server with a placeholder for the variable.
2. The variable definition with the name matching the placeholder in the URL.




## Security Configuration

Define security schemes. The specified default security scheme is automatically used for all protected routes - unless specified otherwise.

```kotlin
install(OpenApi) {
    security {
        securityScheme("MySecurityScheme") { // (1)!
            type = AuthType.HTTP
            scheme = AuthScheme.BASIC
        }
        defaultSecuritySchemeNames("MySecurityScheme") // (2)!
        defaultUnauthorizedResponse { // (3)!
            description = "Username or password is invalid"
        }
    }
}
```

1. Define a new security scheme with a unique name `"MySecurityScheme"`. Multiple schemes can be defined.
2. Use the security scheme with the name `"MySecurityScheme"` for all protected routes - unless specified otherwise.
3. Describe and add a default unauthorized response to the documentation or all protected routes.




## Tags Configuration

Tags are used to organize and group operations. Their description and external documentation can be defined in this configuration section.
Tags do not need to be defined explicitly in the plugin configuration if no additional information is required.

```kotlin
install(OpenApi) {
    tags {
        tag("users") {
            description = "routes to manage users"
            externalDocUrl = "example.com"
            externalDocDescription = "Users documentation"
        }
        tag("documents") {
            description = "routes to manage documents"
        }
    }
}
```

**Automatically Assigning Tags**

The `tagGenerator` function automatically assigns tags to routes based on their URL or other properties. Routes can still provide their own
additional tags.

```kotlin
install(OpenApi) {
    tags {
        tagGenerator = { url -> // (1)!
            when {
                url.firstOrNull() === "user" -> listOf("users")
                url.firstOrNull() === "document" -> listOf("documents")
                else -> listOf()
            }
        }
    }
}
```

1. For an example route `document/settings/access` the `url` parameter would contain `["document", "settings", "access"]`.




## External Documentation

Reference to external documentation sources.

```kotlin
install(OpenApi) {
    externalDocs {
        url = "https://docs.example.com"
        description = "Complete API documentation"
    }
}
```




## Output Format

Choose between JSON and YAML output:

```kotlin
install(OpenApi) {
    outputFormat = OutputFormat.JSON //(1)!
}
```

1. Available options are `OutputFormat.JSON` and `OutputFormat.YAML`.




## Route Filtering

The `pathFilter` function determines which routes are included in the OpenAPI specification. It takes the http method and the segmented url
and returns whether it should be included (true) or omitted (false).

```kotlin
pathFilter = { method, url -> // (1)!
    url.firstOrNull() != "internal" // (2)!
}
```

1. For an example route `GET document/settings/access` the `method` is `HttpMethod.Get` and 
   the `url` parameter would contain `["document", "settings", "access"]`.
2. Excludes `/internal` routes.

Individual routes can also be excluded using the hidden flag in route documentation.

```kotlin
get("secret", {
    hidden = true
}) {
    //...
}
```




## Ignored Route Selectors

Ktor plugins may add route selectors that should not appear in the OpenAPI specification. These selectors can be filtered out, hiding them
in the generated urls.

```kotlin
install(OpenApi) {
    
    ignoredRouteSelectors = // (1)!
        ignoredRouteSelectors
        + RateLimitRouteSelector::class;

    ignoredRouteSelectorClassNames =  // (2)!
        ignoredRouteSelectorClassNames
        + "io.ktor.server.plugins.ratelimit.RateLimitRouteSelector"
}
```

1. Ignore a route selector by class reference.
2. Ignore a route selector by class name (useful for internal/inaccessible classes)




## Schema Configuration

Control how types are converted to OpenAPI schemas:

```kotlin
schemas {
    generator = SchemaGenerator.kotlinx() // (1)!
    schema<User>("user") // (2)!
}
```

1. Available pre-built generators are `SchemaGenerator.reflection()` (default) and `SchemaGenerator.kotlinx()`.
2. Define global schemas.

??? info "More Information"

    More information on schemas and schema generation can be found here:

    [:octicons-arrow-right-24: Introduction To Schema](./working_with_schemas/schema_introduction.md)




## Example Configuration

Control how examples are encoded:

```kotlin
examples {
    encoder = ExampleEncoder.internal() // (1)!
    example("user") { value = User("Mr. Example") } // (2)!
}
```

1. Available pre-built encoders are `ExampleEncoder.internal()` (default) and `ExampleEncoder.kotlinx()`.
2. Define global examples.

??? info "More Information"

    More information on examples and example encoding can be found here:

    [:octicons-arrow-right-24: Introduction To Examples](./working_with_examples/examples_introduction.md)




## Multiple Specifications

The plugin can generate multiple independent OpenAPI specifications from a single application. Each specification is identified by a unique
name and can have its own configuration.

Routes are assigned to specifications either explicitly (via specName property) or automatically (via assigner function). Each specification
is generated and handled independently.

````kotlin
install(OpenApi) { // (1)!
    info {
        title = "Example API"
        description = "An example api."
    }
    spec("v1") { // (2)!
        info {
            version = "1.0"
        }
    }
    spec("v2") { // (3)!
        info {
            version = "2.0"
        }
    }
}

routing {
    route("api-v1.json") {  // (4)!
        openApi("v1")
    }
    route("api-v2.json") { // (5)!
        openApi("v2")
    }
}
````

1. Base configuration applies to all specifications.
2. Configuration specific to "v1" specification.
3. Configuration specific to "v2" specification.
4. Expose the "v1" specification.
5. Expose the "v2" specification.

??? info "More Information"

    More information on handling multiple API specifications can be found here:

    [:octicons-arrow-right-24: Multiple OpenAPI Specifications](./advanced_topics/multiple_openapi_specifications.md)
