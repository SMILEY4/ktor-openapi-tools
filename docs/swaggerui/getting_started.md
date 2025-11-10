# Getting Started

## Add Dependency

Add the `ktor-swagger-ui` dependency to your project:

=== "Gradle (Kotlin)"
    ```kotlin
    implementation("io.github.smiley4:ktor-swagger-ui:$version")
    ```

=== "Gradle"
    ```groovy
    implementation 'io.github.smiley4:ktor-swagger-ui:$version'
    ```

=== "Maven"
    ```xml
    <dependency>
        <groupId>io.github.smiley4</groupId>
        <artifactId>ktor-swagger-ui</artifactId>
        <version>${version}</version>
    </dependency>
    ```




## Basic Usage

Swagger UI is served through standard Ktor routes using the swaggerUI() function. The function accepts the URL of an OpenAPI specification.

```kotlin
routing {
    
    route("swagger") { //(1)!
        swaggerUI("/api.json") { //(2)!
            //...(3)
        }
    }
    
}
```

1. Specify route to serve Swagger UI at `/swagger`.
2. Expose Swagger UI using the OpenAPI specification at `/api.json`.
3. Add configuration for this Swagger UI instance here.




## With Generated Specifications

When using the [ktor-openapi plugin](../openapi/getting_started.md), you can serve the automatically generated OpenAPI specification and point Swagger UI to it.

```kotlin
routing {
    
    route("api.json") {
        openApi() //(1)!
    }
    route("swagger") {
        swaggerUI("/api.json") //(2)!
    }
    
}
```

1. Serve auto-generated OpenAPI specification at `/api.json`.
2. Expose Swagger UI using auto-generated specification at `/api.json`.




## Multiple Specifications

Swagger UI can display multiple OpenAPI specifications within a single interface. Users can switch between different APIs using a dropdown selector
in the top navigation bar. This is particularly useful for versioned APIs or when documenting multiple related services.

```kotlin
routing {
    route("swagger") {
        swaggerUI(mapOf(
            "API v1" to "/v1/api.json",
            "API v2" to "/v2/api.json"
        ))
    }
}
```

The map keys become the display names in the dropdown, while the values are the URLs to the respective specifications.

Alternatively, you can specify multiple independent Swagger UI instances.

```kotlin
routing {

    route("v1") {
        route("swagger") {
            swaggerUI("/v1/api.json")
        }
    }
    route("v2") {
        route("swagger") {
            swaggerUI("/v2/api.json")
        }
    }
    
}
```




## Configuration

Swagger UI provides extensive configuration options to customize its behavior and appearance.

```kotlin
route("swagger") {
    
    swaggerUI("/api.json") {
        displayOperationId = false
        operationsSorter = OperationsSort.HTTP_METHOD
        syntaxHighlight = SwaggerUISyntaxHighlight.MONOKAI
        tryItOutEnabled = true
        // ...
    }
    
}
```

??? info "Configuration Options"

    For more information on available configuration options, please see the [api reference](../dokka/ktor-swagger-ui/ktor-swagger-ui/io.github.smiley4.ktorswaggerui.config/-swagger-u-i-config/index.html).