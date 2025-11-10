The OpenAPI plugin operates as a Ktor plugin that integrates with the routing system to collect API documentation and generate OpenAPI
specifications. The plugin automatically finds all registered routes, extracts documentation, generates schemas for types, and
produces a complete OpenAPI specification that can be served via HTTP endpoints.

The plugin follows a non-invasive design pattern. Standard Ktor routes continue to function normally, while routes using the documented
routing functions are included in the generated specification. This allows for gradual documentation without requiring changes to
existing code.




## Add Dependencies

To generate OpenAPI specifications, the `ktor-openapi` artifact must be included in the build script.

=== "Gradle (Kotlin DSL)"

    ```kotlin
    implementation("io.github.smiley4:ktor-openapi:$version")
    ```

=== "Gradle (Groovy)"

    ```groovy
    implementation 'io.github.smiley4:ktor-openapi:$version'
    ```

=== "Maven"

    ```xml
    <dependency>
        <groupId>io.github.smiley4</groupId>
        <artifactId>ktor-openapi</artifactId>
        <version>${version}</version>
    </dependency>
    ```

??? tip "Ktor Compatibility and Previous Versions"

    This project as been split into multiple projects starting with version 5.0.</br>Versions up to 5.0 are called `ktor-swagger-ui` instead of `ktor-openapi`.

    | Ktor | Plugin Version | Project Name      |
    |------|----------------|-------------------|
    | 2.x  | up to 3.x      | `ktor-swagger-ui` |
    | 3.x  | 4.x            | `ktor-swagger-ui` |
    | 3.x  | 5.x            | `ktor-openapi`    |




## Installing the OpenAPI Plugin

The OpenAPI plugin is installed in the Ktor application using the standard `install` function:

```kotlin
install(OpenApi) { //(1)!
    //...(2)
}
```

1. Install the "OpenAPI" plugin to the application.
2. Additional plugin configuration goes here.

The plugin registers itself with the application and collects documentation from routes. The OpenAPI specification is then generated based
on the collected information at startup.

While not required for getting started, you can configure basic information about the API as well as the behavior of the generation:

```kotlin
install(OpenApi) {
    info {
        title = "My API"
        version = "1.0.0"
        description = "API description"
    }
    outputFormat = OutputFormat.JSON
}
```

??? info "More Information"

    More information on plugin configuration can be found here:

    [:octicons-arrow-right-24: Plugin Configuration](./plugin_configuration.md)

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-open-api-plugin-config/index.html)




## Creating a Documented Route

The plugin provides documented versions of Ktor's standard routing functions. These functions accept the same parameters as their standard
counterparts, plus an additional documentation block:

```kotlin
import io.github.smiley4.ktoropenapi.get //(1)!
// ...

fun Application.module() {

    install(OpenApi)

    routing {
        get("hello", { // (2)!
            description = "A simple hello world endpoint" // (3)!
            request { // (4)!
                queryParameter<String>("name") { // (5)!
                    description = "The name to greet"
                    required = false
                }
            }
            response { // (6)!
                code(HttpStatusCode.OK) { // (7)!
                    description = "Returns a greeting message"
                    body<String>() // (8)!
                }
            }
        }) { // (9)!
            val name = call.request.queryParameters["name"] ?: "World"
            call.respondText("Hello $name!")
        }
    }
}
```

1. Documented route functions must be imported from `io.github.smiley4.ktoropenapi` instead of `io.ktor.server.routing`.
   The plugin provides documented versions of all standard HTTP method functions: get, post, put, delete, patch, options, head.
2. This `get` function takes three parameters: the route path, the function for the documentation block and function for the route handler.
3. The description field provides a human-readable explanation of the endpoint's purpose.
4. The request block contains documentation for all accepted inputs.
5. Query parameters are documented with their type, description, and requirement status. Schemas are generated automatically.
6. The response block contains documentation for all possible responses.
7. Each HTTP status code can be documented individually.
8. Response body types are specified, with automatic schema generation.
9. The handler block contains standard Ktor route logic and remains unchanged from non-documented routes.

Type information from the documentation DSL is automatically converted to OpenAPI schemas. Schemas can be defined locally (inline with usage)
or globally (in the plugin configuration). The plugin uses the [schema-kenerator](https://github.com/SMILEY4/schema-kenerator) library to generate its schemas from kotlin classes.

**Structure of a generic documented Route:**

```kotlin
httpMethod("path", {
    // Documentation block
    // API interface specification
}) {
    // Handler block  
    // Implementation logic
}
```

??? info "More Information"

    More information on route documentation can be found here:

    [:octicons-arrow-right-24: Basic Route Documentation](./documenting_routes/basic_route_documentation.md)

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-route-config/index.html)




## Exposing the OpenAPI Specification

The generated OpenAPI specification must be explicitly exposed via a route.

The plugin generates the specification internally, but explicit route exposure provides flexibility and control over the path, access
control (i.e. authentication) and availability (e.g. only in development environments)

```kotlin
import io.github.smiley4.ktoropenapi.openApi

fun Application.module() {

    install(OpenApi)

    routing {
        route("api.json") { //(1)!
            openApi() //(2)!
        }
    }
}
```

1. A route is created at the desired specification path. This route behaves like any other ktor route and can be nested in other blocks.
   In this example, the specification is available at `localhost:8080/api.json`.
2. The `openApi()` function serves the generated specification at this route.

??? info "More Information"

    More information on providing and interacting with generated OpenAPI specifications can be found here:

    [:octicons-arrow-right-24: Basic Route Documentation](./providing_openapi_specification.md)
