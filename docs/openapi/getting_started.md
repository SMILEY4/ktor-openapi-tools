# Getting Started

## Add Dependency

To generate OpenAPI specifications, you need to include the `ktor-openapi` artifact in the build script.
All artifacts are published to Maven Central.

=== "Gradle (Kotlin)"
    ```kotlin
    implementation("io.github.smiley4:ktor-openapi:$version")
    ```

=== "Gradle"
    ```groovy
    implementation 'io.github.smiley4:ktor-openapi:$version'
    ```

=== "Gradle (Maven)"
    ```xml
    <dependency>
        <groupId>io.github.smiley4</groupId>
        <artifactId>ktor-openapi</artifactId>
        <version>${version}</version>
    </dependency>
    ```

__Previous Versions__

???+ warning "todo"
    - explain project split & rename
    - compatibility table

    | Ktor | Plugin Version                 |
    |------|--------------------------------|
    | 2.x  | up to 3.x as `ktor-swagger-ui` |
    | 3.x  | 4.x as `ktor-swagger-ui`       |
    | 3.x  | 5.x as `ktor-openapi`          |

## Install OpenAPI

```kotlin
install(OpenApi) { //(1)!
    //...(2)
}
```

1. Install the "OpenAPI" plugin to the application.
2. Add additional plugin configuration here.

??? info "More Information"

    [:octicons-arrow-right-24: Plugin Configuration](plugin_configuration.md)

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-open-api-plugin-config/index.html)


## Exposing OpenAPI Specification

```kotlin
routing {
    route("api.json") { //(1)!
        openApi() //(2)!
    }
}
```

1. Create a new route to expose the OpenAPI specification file at `api.json`.
2. Expose the OpenAPI specification.

??? info "More Information"

    [:octicons-arrow-right-24: Multiple OpenAPI Specifications](multiple_specs.md)

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi/open-api.html)


## Documenting Routes

```kotlin
import io.github.smiley4.ktoropenapi.get //(1)!

get("hello", { //(2)!
    description = "A Hello-World route" //(3)!
    response {
        HttpStatusCode.OK to { //(4)!
            description = "A success response"
            body<String>() //(5)!
        }
    }
    //...
}) {
    call.respondText("Hello World!") //(6)!
}
```

1. Replace `io.ktor.server.routing.get` with `io.github.smiley4.ktoropenapi.get`. Same for other http methods.
2. Enrich `/hello` route with additional information.
3. Add a description to the route.
4. Document the different possible responses.
5. Specify the response body type. The schema for the type is generated automatically.
6. Handle requests as usual.

??? info "More Information"

    [:octicons-arrow-right-24: Documenting Routes](documenting_routes.md)

    [:octicons-arrow-right-24: Documentation with Type-safe Routing](typesafe_routing.md)

    [:octicons-arrow-right-24: API Reference](../dokka/ktor-openapi/ktor-openapi/io.github.smiley4.ktoropenapi.config/-route-config/index.html)
