# Type-Safe Routing Support

The OpenAPI plugin supports Ktor's type-safe routing via the Resources plugin. Resource classes can be used to define routes and the plugin
can automatically extract parameter information from resource class properties.



## Setup

Type-safe routing support requires additional configuration:


### Installed Resources Plugin

The Ktor Resources plugin must be installed separately:

```kotlin
dependencies {
    implementation("io.ktor:ktor-server-resources:$ktor_version")
}
```

```kotlin
install(Resources)
install(OpenApi)
```


### Configure Schema Generator

Type-safe routing requires the kotlinx.serialization schema generator:

```kotlin
install(OpenApi) {
    schemas {
        generator = SchemaGenerator.kotlinx()
    }
}
```


### Enable Auto-Documentation (Optional)

The `autoDocumentResourcesRoutes` option automatically extracts parameter information from resource classes:

```kotlin
install(OpenApi) {
    autoDocumentResourcesRoutes = true
}
```

When enabled, the plugin automatically documents:

- Path parameters from resource class properties
- Query parameters from resource class properties
- Parameter types, nullability, and default values

This reduces the need for manual parameter documentation in resource routes.



## Documenting Resource Routes

### Import Documented Resource Functions

Use documented resource route functions from `io.github.smiley4.ktoropenapi.resources`:

```kotlin
import io.github.smiley4.ktoropenapi.resources.get
import io.github.smiley4.ktoropenapi.resources.post
import io.github.smiley4.ktoropenapi.resources.put
import io.github.smiley4.ktoropenapi.resources.delete
// ...
```


### Document Resource Routes

The documentation block works the same as with standard routes:

```kotlin
import io.github.smiley4.ktoropenapi.resources.get

@Serializable
@Resource("users")
class Users

@Serializable
@Resource("{id}")
class User(val parent: Users, val id: String)

get<User>({
    description = "Retrieve a user by ID"
    tags = listOf("users")
    response {
        code(HttpStatusCode.OK) {
            description = "User found"
            body<UserResponse>()
        }
        code(HttpStatusCode.NotFound) {
            description = "User not found"
        }
    }
}) {
    // handler implementation
}
```