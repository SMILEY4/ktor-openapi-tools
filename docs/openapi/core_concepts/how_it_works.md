# How It Works

## Overview

The OpenAPI plugin operates as a Ktor plugin that integrates with the routing system to collect API documentation and generate OpenAPI
specifications. The plugin automatically finds all registered routes, extracts documentation, generates schemas for types, and
produces a complete OpenAPI specification that can be served via HTTP endpoints.

The plugin follows a non-invasive design pattern: standard Ktor routes continue to function normally, and routes using the documented
routing functions are included in the generated specification. This allows for gradual documentation without requiring changes to
existing code.

## Key Concepts

### Documentation DSL

The plugin provides a domain-specific language for documenting routes. The DSL is structured as nested configuration blocks:

```kotlin
httpMethod("path", {
    // Route-level configuration
    description = "..."
    operationId = "..."
    tags = listOf(...)

    request {
        // Request documentation
        pathParameter<Type>("name") { }
        queryParameter<Type>("name") { }
        headerParameter<Type>("name") { }
        body<Type>() { }
    }

    response {
        // Response documentation
        code(HttpStatusCode.OK) {
            body<Type>() { }
            headerParameter<Type>("name") { }
        }
    }
}) {
    // Route handler
}
```

### Schema Generation

Type information from the documentation DSL is automatically converted to OpenAPI schemas. Schemas can be defined locally (
inline with usage) or globally (in the plugin configuration).
The plugin uses [schema-kenerator](https://github.com/SMILEY4/schema-kenerator) to generate its schemas from kotlin classes and supports
multiple schema generation strategies:

- Reflection-based (default): Analyzes Kotlin types using reflection
- Kotlinx.Serialization-based: Uses serialization descriptors for schema generation

Schema generation behavior can be configured and customized in the plugin configuration:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx(json) //(1)!
    }
}
```

1. Configure the schema generation to use the kotlinx-serializer configuration preset.

### Examples

Example objects provided in the documentation DSL are encoded and embedded in the OpenAPI specification. Examples can be defined locally (
inline with usage) or globally (in the plugin configuration).

````kotlin
install(OpenApi) {
    examples {
        example("global-user-example") { //(1)!
            description = "An example user."
            value = User(id = "123", name = "John")
        }
    }
}

// ...

body<User>() {
    example("Example User") { //(2)!
        description = "An example user."
        value = User(id = "123", name = "John")
    }
}

body<User>() {
    exampleRef("Example User", "global-user-example") //(3)
}
````

1. Define a global example with the id `global-user-example`.
2. Document a request or response body with a local example user.
3. Document a request or response body using the globally defined user, referenced by its id `global-user-example`.

### Specifications

The plugin can generate multiple independent OpenAPI specifications from a single application. Each specification is identified by a unique
name and can have its own configuration.

Routes are assigned to specifications either explicitly (via specName property) or automatically (via assigner function). Each specification
is generated and handled independently.

````kotlin
install(OpenApi) {
    // Base configuration applies to all specs
    
    spec("v1") {
        // Configuration specific to "v1" specification
    }
    
    spec("v2") {
        // Configuration specific to "v2" specification
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


## Plugin Lifecycle

**1. Route Registration**

Standard Ktor routes and documented routes are registered with the application routing system. Both route types register normally with
Ktor's routing system, while documented routes include an additional `RouteSelector` with information for the OpenAPI specification.

**2. Route Collection**

After application initialization, the plugin traverses the routing structure and extracts documentation from annotated routes.

**3. Specification Generation**

The collected route information is transformed into an OpenAPI specification and stored in memory. Schemas are automatically generated for
referenced types and example values are encoded.

**4. Ktor Application Startup Completed**

The application is ready to process requests.

**5. Specification Serving**

The previously generated and stored OpenAPI specification is being served at the specified route.

## Interacting with the OpenAPI Specification

The plugin provides programmatic access to generated specifications.

**Getting a Specification**

Retrieve the generated and stored specifications.

```kotlin
val defaultSpec = OpenApiPlugin.getOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID) //(1)!
val v2Spec = OpenApiPlugin.getOpenApiSpec("v2") //(2)!
```

1. Get the default specification.
2. Get a named specification, usually when multiple specifications are configured.

**Regenerating a Specification**

Discard a previously created and stored specification. Collect all routes and information again and generate a new specification.

```kotlin
OpenApiPlugin.regenerateOpenApiSpec(OpenApiPluginConfig.DEFAULT_SPEC_ID) //(1)!
OpenApiPlugin.regenerateOpenApiSpec("v2") //(2)!
```

1. Regenerate the default specification.
2. Regenerate a named specification, usually when multiple specifications are configured.