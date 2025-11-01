# Plugin Configuration

The OpenAPI plugin and specification is configured in the installation block. All configuration is optional - the plugin functions with
default settings, though providing API metadata is recommended.

## OpenAPI Specification Content

Provide metadata for the api that directly populates fields in the generated OpenAPI specification.

### Info Section

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

### Servers Section

The servers section defines base URLs where the API is available. Multiple servers can be specified and server variables are supported for
dynamic server configuration.

```kotlin
install(OpenApi) {
    server {
        url = "localhost"
        description = "local dev-server"
    }
    server {
        url = "example.com"
        description = "productive server"
    }
}
```

### External Documentation

Reference to external documentation sources.

```kotlin
install(OpenApi) {
    externalDocs {
        url = "example.com"
        description = "Project documentation"
    }
}
```

### Security Section

Define security schemes and default security requirements. The default security scheme name is automatically used for all protected routes -
unless specified otherwise.

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

### Tags

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
            externalDocUrl = "example.com"
            externalDocDescription = "Document documentation"
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

## Route Handling

These configuration options control which routes are included in the specification and how they are processed.

### Filter Routes

The `pathFilter` function determines which routes are included in the OpenAPI specification. It takes the http method and the segmented url
and returns whether it should be included (true) or omitted (false).

```kotlin
install(OpenApi) {
    pathFilter = { _, url -> url.firstOrNull() == "api" }
}
```

**Alternative: Hidden Routes**

Individual routes can also be excluded using the hidden flag in route documentation.

```kotlin
get("secret", {
    hidden = true
}) {
    //...
}
```

### Ignored Route Selectors

Ktor plugins may add route selectors that should not appear in the OpenAPI specification. These selectors can be filtered out, hiding them
in the generated urls.

```kotlin
install(OpenApi) {
    // Ignore by class reference
    ignoredRouteSelectors = ignoredRouteSelectors + RateLimitRouteSelector::class

    // Ignore by class name (useful for internal/inaccessible classes)
    ignoredRouteSelectorClassNames = ignoredRouteSelectorClassNames + "io.ktor.server.plugins.ratelimit.RateLimitRouteSelector"
}
```

## Schema Configuration

Schemas describe the types used in the API. Schemas can be specified manually or generated automatically from Kotlin types.

```kotlin
install(OpenApi) {
    schemas {
        // Define global schemas
        schema<User>("user-schema")
        schema<Product>("product-schema")

        // Configure schema generation
        generator = SchemaGenerator.reflection()
    }
}
```

**Global Schemas**

Global schemas are defined once in the plugin configuration and can be referenced throughout route documentation.

```kotlin
schemas {
    // From type parameter
    schema<User>("user")
    schema<Product>("product")
    schema<ErrorResponse>("error")

    // From KType
    schema("user-list", typeOf<List<User>>())

    // From Swagger Schema object
    schema("custom", Schema<Any>().apply {
        type = "object"
        properties = mapOf(/* ... */)
    })
}

// ...

get("users", {
    response {
        code(HttpStatusCode.OK) {
            body(ref("user-list"))
        }
        default {
            body(ref("error"))
        }
    }
}) { }
```

**Configuring Automatic Schema Generation***

The schema generator determines how Kotlin types are converted to OpenAPI schemas. Two strategies are available:

- *Reflection*: Uses Kotlin reflection to analyze types (default)
- *Kotlinx.Serialization*: Uses serialization descriptors (requires @Serializable annotations)

Both built-in generators support additional configuration.

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.reflection {
            // configure schema generator
        }
        generator = SchemaGenerator.kotlinx {
            // configure schema generator
        }
    }
}
```

For more information on schema handling, see [Schema Generation Overview](./../working_with_schemas/schema_generation_overview.md).

## Example Handling

The plugin encodes example objects and includes them in the OpenAPI specification. Example encoding behavior is configured in the examples
block.

```kotlin
install(OpenApi) {
    examples {
        // Configure example encoder
        encoder = ExampleEncoder.internal()

        // Define global examples
        example("default-user") {
            value = User(id = "123", name = "John")
            description = "A typical user"
        }
    }
}
```

**Global Examples**

Global examples are defined once in the plugin configuration and can be referenced throughout route documentation.

```kotlin
examples {
    example("success-user") {
        value = User(id = "1", name = "Alice", role = "admin")
        summary = "Admin user"
        description = "Example of an administrative user"
    }

    example("error-not-found") {
        value = ErrorResponse(code = 404, message = "Resource not found")
        summary = "Not found error"
    }

    example("error-unauthorized") {
        value = ErrorResponse(code = 401, message = "Unauthorized")
        summary = "Auth error"
    }
}

// ...

get("users/{id}", {
    response {
        code(HttpStatusCode.OK) {
            body<User>() {
                exampleRef("Example User", "success-user")
            }
        }
        code(HttpStatusCode.NotFound) {
            body<ErrorResponse>() {
                exampleRef("Not Found", "error-not-found")
            }
        }
        code(HttpStatusCode.Unauthorized) {
            body<ErrorResponse>() {
                exampleRef("Unauthorized", "error-unauthorized")
            }
        }
    }
}) { }
```

**Example Encoder**

The example encoder determines how Kotlin objects are serialized for inclusion in the specification. The build-in encoding strategies are
provided:

- *Internal*: Uses Swagger's built-in encoder (default)
- *Kotlinx.Serialization*: Uses kotlinx.serialization with configurable Json instance
- *Custom*: Fully customizable example encoding

```kotlin
examples {
    // Swagger internal encoder (default)
    encoder = ExampleEncoder.internal()

    // Kotlinx.Serialization encoder
    encoder = ExampleEncoder.kotlinx()

    // Kotlinx.Serialization with custom Json configuration
    encoder = ExampleEncoder.kotlinx(Json {
        prettyPrint = true
        encodeDefaults = true
        namingStrategy = JsonNamingStrategy.SnakeCase
    })

    // Custom encoder
    encoder = { type, example ->
        // Custom encoding logic
        example.toString()
    }
}
```

For more information on example handling, see [Working With Examples](./../working_with_examples).

## Multiple OpenAPI Specifications

The plugin supports generating multiple independent OpenAPI specifications from a single application. Each specification has its own
configuration that overwrites or extends the base plugin configuration. Each defined specification is generated and provided independently
as separate files.

```kotlin
install(OpenApi) {
    // Base configuration - applies to all specifications
    info {
        title = "My API"
    }

    // Specification-specific configuration
    spec("v1") {
        info {
            version = "1.0.0"
            description = "API Version 1"
        }

        // Spec-specific schema configuration
        schemas {
            schema<UserV1>("user-v1")
        }
    }

    spec("v2") {
        info {
            version = "2.0.0"
            description = "API Version 2"
        }

        // Spec-specific schema configuration
        schemas {
            schema<UserV2>("user-v2")
        }
    }
}
```

**Assigning Routes to Specifications**

Routes are assigned to specifications using the `specName` property or an assigner function.

*Manual Assignment:*

```kotlin
get("v1/users", {
    specName = "v1" // Assign to "v1" specification
}) { }

get("v2/users", {
    specName = "v2" // Assign to "v2" specification
}) { }
```

*Automatic Assignment:*

```kotlin
install(OpenApi) {
    // Assign routes based on URL structure
    specAssigner = { url, tags ->
        when (url.firstOrNull()) {
            "v1" -> "v1"
            "v2" -> "v2"
            else -> OpenApiPluginConfig.DEFAULT_SPEC_ID
        }
    }
}
```

Each specification must be exposed via its own route:

```kotlin
routing {
    route("v1/api.json") {
        openApi("v1")
    }

    route("v2/api.json") {
        openApi("v2")
    }

    route("internal/api.json") {
        openApi("internal")
    }
}
```

For more information on multiple specifications,
see [Multiple OpenAPI Specifications](./../advanced_topics/multiple_openapi_specifications.md).
