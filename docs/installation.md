# Installation

```kotlin
dependencies {
    // Defining and generating OpenAPI spec
    implementation("io.github.smiley4:ktor-openapi:<VERSION>")
    
    // Configuring and serving Swagger UI
    implementation("io.github.smiley4:ktor-swagger-ui:<VERSION>")
    
    // Configuring and serving ReDoc
    implementation("io.github.smiley4:ktor-redoc:<VERSION>")
}
```

!!! Recommendation
    All dependencies are optional and can work independently, but we recommend installing and using them together


## Ktor compatibility

- `Ktor 2.x`: `ktor-swagger-ui` up to 3.x
- `Ktor 3.x`: `ktor-swagger-ui` with 4.x
- `Ktor 3.x`: `ktor-openapi`, `ktor-swagger-ui` and `ktor-redoc` starting with 5.x
