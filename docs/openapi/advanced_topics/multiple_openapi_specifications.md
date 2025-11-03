# Multiple API Specifications

The OpenAPI plugin supports generating multiple independent specifications from a single application.
This enables API versioning, separation of internal and external APIs, or organizing endpoints by product or team.

Each specification has a unique identifier, its own configuration, and contains only the routes assigned to it.
Specifications are generated and served independently.




## Configuring Specifications

Each specification can have its own configuration that extends or overrides the base plugin configuration.

All configuration options available in the base plugin config are available for individual specifications:

```kotlin
install(OpenApi) {
    info { // (1)!
        title = "My API"
    }
    spec("v1") { // (2)!
        info {
            version = "1.0.0"
        }
    }
    spec("v2") { // (3)!
        info {
            version = "2.0.0"
        }
    }
}
```

1. Base configuration for all specifications
2. Define and configure specification with identifier "v1"
3. Define and configure specification with identifier "v2"

Each specification is identified by a unique string (e.g., "v1", "v2", "internal").




## Assigning Routes to Specifications

Routes must be assigned to specifications to appear in them. Assignment can be done explicitly in route documentation or automatically via an assigner function.


### Assignment at Route Documentation

The `specName` property assigns a route to a specific specification:

```kotlin
get("users", {
    specName = "v1"
    description = "Get users (v1)"
}) { }
```

Routes can be easily assigned in groups by adding the `specName` at a parent route:

```kotlin
route("v1", {
    specName = "v1" // (1)!
}) {
    get("users") { }
    get("products") { }
    get("orders") { }
}
```

1.  All child routes are assigned to "v1".

All routes within this block are automatically assigned to the "v1" specification through inheritance.


### Assignment via Assigner Function

Routes can be automatically assigned based on their properties using an assigner function:

```kotlin
install(OpenApi) {
    specAssigner = { url, tags ->
        when (url.firstOrNull()) {
            "v1" -> "v1"
            "v2" -> "v2"
            "internal" -> "internal"
            else -> OpenApiPluginConfig.DEFAULT_SPEC_ID
        }
    }
}
```

The function receives:

- `url`: URL path as a list of segments (e.g., `["api", "v1", "users"]` for `/api/v1/users`)
- `tags`: Tags assigned to the route

The function returns the specification identifier to assign the route to.


### Unassigned Routes

Routes without an assignment are assigned to the default specification with the identifier `OpenApiPluginConfig.DEFAULT_SPEC_ID`.
The default specification only exists if any routes are assigned to it.




## Serving Multiple Specifications

Each specification is served independently via HTTP routes.

```kotlin
routing {
    route("v1/api.json") { // (1)!
        openApi("v1")
    }
    route("v2/api.json") { // (2)!
        openApi("v2")
    }
    route("internal/api.json") { // (3)!
        openApi("internal")
    }
}
```

1. Expose v1 specification.
2. Expose v2 specification.
3. Expose internal specification.

??? info "Multiple Specifications with Swagger UI and Redoc"

    More information on how to handle multiple specifications with documentation UIs:

    [:octicons-arrow-right-24: Swagger UI](./../../swaggerui/getting_started.md)

    [:octicons-arrow-right-24: ReDoc](./../../redoc/getting_started.md)
