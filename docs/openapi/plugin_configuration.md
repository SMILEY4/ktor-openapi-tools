# Plugin Configuration

The OpenAPI plugin is configured during installation. All configuration is optional and the plugin works with default settings, though providing basic API information is recommended.

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
    url = "https://{environment}.example.com"
    
    variable("environment") {
        default = "api"
        enum = listOf("api", "staging", "dev")
        description = "Environment name"
    }
}
```

## Security Configuration

Define security schemes. The specified default security scheme is automatically used for all protected routes - unless specified otherwise.

```kotlin
install(OpenApi) {
    security {
        securityScheme("MySecurityScheme") {
            type = AuthType.HTTP
            scheme = AuthScheme.BASIC
        }
        defaultSecuritySchemeNames("MySecurityScheme")
        defaultUnauthorizedResponse {
            description = "Username or password is invalid"
        }
    }
}
```

## Tags Configuration

Tags are used to organize and group operations. Their description and external documentation can be defined in this configuration section. Tags do not need to be defined explicitly in the plugin configuration if no additional information is required.

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
            externalDocUrl = "example.com"
            externalDocDescription = "Document documentation"
        }
    }
}
```

**Automatically Assigning Tags**

The `tagGenerator` function automatically assigns tags to routes based on their URL or other properties. Routes can still provide their own additional tags.

```kotlin
install(OpenApi) {
    tags {
        tagGenerator = { url ->
            when {
                url.firstOrNull() === "user" -> listOf("users")
                url.firstOrNull() === "document" -> listOf("documents")
                else -> listOf()
            }
        }
    }
}
```

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
    outputFormat = OutputFormat.JSON
    outputFormat = OutputFormat.YAML
}
```

## Route Filtering

The `pathFilter` function determines which routes are included in the OpenAPI specification. It takes the http method and the segmented url and returns whether it should be included (true) or omitted (false).

```kotlin
pathFilter = { method, url ->
    url.firstOrNull() != "internal" // Exclude /internal routes
}
```

Individual routes can also be excluded using the hidden flag in route documentation.

```kotlin
get("secret", {
    hidden = true
}) {
    //...
}
```

## Ignored Route Selectors

Ktor plugins may add route selectors that should not appear in the OpenAPI specification. These selectors can be filtered out, hiding them in the generated urls.

```kotlin
install(OpenApi) {
    // Ignore by class reference
    ignoredRouteSelectors = ignoredRouteSelectors + RateLimitRouteSelector::class

    // Ignore by class name (useful for internal/inaccessible classes)
    ignoredRouteSelectorClassNames = ignoredRouteSelectorClassNames + "io.ktor.server.plugins.ratelimit.RateLimitRouteSelector"
}
```

## Schema Configuration

Control how types are converted to OpenAPI schemas:

```kotlin
schemas {
    generator = SchemaGenerator.kotlinx() // or reflection()
    schema<User>("user") // Define global schemas
}
```

See [Schema Generation]() for details.

## Example Configuration

Control how examples are encoded:

```kotlin
examples {
    encoder = ExampleEncoder.internal() // or kotlinx()
    example("user") { value = User(...) } // Define global examples
}
```

See [Examples]() for details.


## Multiple Specifications

The plugin can generate multiple independent OpenAPI specifications from a single application. Each specification is identified by a unique
name and can have its own configuration.

Routes are assigned to specifications either explicitly (via specName property) or automatically (via assigner function). Each specification
is generated and handled independently.

````kotlin
install(OpenApi) {
    // Base configuration applies to all specs
    info {
        title = "Example API"
        description = "An example api."
    }
    
    spec("v1") {
        // Configuration specific to "v1" specification
        info {
            version = "1.0"
        }
    }

    spec("v2") {
        // Configuration specific to "v2" specification
        info {
            version = "2.0"
        }
    }
}

routing {

    // expose the "v1" specification
    route("api-v1.json") {
        openApi("v1")
    }

    // expose the "v2" specification
    route("api-v2.json") {
        openApi("v2")
    }

}
````

See [Multiple Specifications]() for details.