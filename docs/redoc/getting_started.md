# Getting Started

## Add Dependency

Add the `ktor-redoc` dependency to your project:

=== "Gradle (Kotlin)"
    ```kotlin
    implementation("io.github.smiley4:ktor-redoc:$version")
    ```

=== "Gradle"
    ```groovy
    implementation 'io.github.smiley4:ktor-redoc:$version'
    ```

=== "Maven"
    ```xml
    <dependency>
        <groupId>io.github.smiley4</groupId>
        <artifactId>ktor-redoc</artifactId>
        <version>${version}</version>
    </dependency>
    ```




## Basic Usage

ReDoc is served through standard Ktor routes using the redoc() function. The function accepts the URL of an OpenAPI specification.

```kotlin
routing {
    route("redoc") {
        redoc("/api.json")
    }
}
```

1. Specify route to serve Redoc at `/redoc`.
2. Expose ReDoc using the OpenAPI specification at `/api.json`.
3. Add configuration for this ReDoc instance here.




## With Generated Specifications

When using the [ktor-openapi plugin](../openapi/getting_started.md), you can serve the automatically generated OpenAPI specification and point ReDoc to it.

```kotlin
routing {
    
    route("api.json") {
        openApi() //(1)!
    }
    route("redoc") {
        redoc("/api.json") //(2)!
    }
    
}
```

1. Serve auto-generated OpenAPI specification at `/api.json`.
2. Expose ReDoc using auto-generated specification at `/api.json`.




## Multiple Specifications

ReDoc works best with separate documentation pages for different specifications. Each route serves an independent ReDoc instance with its
own specification, allowing users to navigate directly to the documentation version they need.

```kotlin
routing {
    route("docs/v1") {
        redoc("/v1/api.json")
    }
    route("docs/v2") {
        redoc("/v2/api.json")
    }
}
```




## Configuration

ReDoc provides extensive configuration options to customize its appearance and behavior.

```kotlin
route("redoc") {
    
    redoc("/api.json") {
        pageTitle = "Redoc - My Api"
        disableSearch = false
        expandResponses = listOf("all")
        hideDownloadButton = false
        pathInMiddlePanel = true
        requiredPropsFirst = true
        sortOperationsAlphabetically = true
        theme = """
          {
            "sidebar": {
              "backgroundColor": "lightblue"
            },
            "rightPanel": {
              "backgroundColor": "darkblue"
            }
          }
        """.trimIndent()
    }
    
}
```

??? info "Configuration Options"

    For more information on available configuration options, please see the [api reference](../dokka/ktor-redoc/ktor-redoc/io.github.smiley4.ktorredoc.config/-redoc-config/index.html).