# Getting Started

## Add Dependency

To serve ReDoc, you need to include the `ktor-redoc` artifact in the build script.
All artifacts are published to Maven Central.

=== "Gradle (Kotlin)"
    ```kotlin
    implementation("io.github.smiley4:ktor-redoc:$version")
    ```

=== "Gradle"
    ```groovy
    implementation 'io.github.smiley4:ktor-redoc:$version'
    ```

=== "Gradle (Maven)"
    ```xml
    <dependency>
        <groupId>io.github.smiley4</groupId>
        <artifactId>ktor-redoc</artifactId>
        <version>${version}</version>
    </dependency>
    ```

## Usage

```kotlin
routing {
    
    route("redoc") { //(1)!
        redoc("/api.json") { //(2)!
            //...(3)
        }
    }
    
}
```

1. Specify route to serve ReDoc at `/redoc`.
2. Expose ReDoc showing the OpenAPI specification at `/api.json`. The url can be relative pointing to specification provided by this application or absolute pointing to an external resource.
3. Add configuration for this ReDoc "instance" here.

??? tip "Using ReDoc with [auto-generated](../openapi/index.md) OpenAPI specification"

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

??? info "Configuration Options"

    For more information on available configuration options, please see the [api reference](../dokka/ktor-redoc/ktor-redoc/io.github.smiley4.ktorredoc.config/-redoc-config/index.html).