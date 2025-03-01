# Multiple OpenAPI Specifications

The OpenAPI plugin support generating multiple OpenAPI specifications from a single application.
This can be useful for example when versioning endpoints or separate internal and external APIs.

Each specification has a unique name used to configure the specification and assign routes to it.


## Configuring Specifications

Specifications do not need to be mentioned in the plugin configuration to function.
The normal plugin config serves as a base for all specs, which can then be overwritten.
The available configuration options for specific OpenAPI specifications are the same as for the base plugin configuration. 

```kotlin
install(OpenAPI) {
    info {
        title = "Example API" //(1)!
    }
    spec("version1") { //(2)!
        info {
            version = "1.0" //(3)!
        }
    }
    spec("version2") { //(4)!
        info {
            version = "2.0" //(5)!
        }
    }
}
```

1. Base plugin configuration defining the title of the api.
2. Define configuration for a specification with name `version1`. The specification inherits the configuration from the base plugin config and further modifications added here will only affect the specification with this name while overwriting the base configuration.
3. Set the version of the specification with name `version1` to "1.0".
4. Define configuration for a specification with name `version2`. The specification inherits the configurations from the base plugin config and further modifications added here will only affect the specification with this name while overwriting the base configuration.
5. Set the version of the specification with name `version2` to "2.0".


## Assigning Routes

Routes can be assigned to specifications either via a flag at the route documentation or via a function in the plugin configuration.

=== "Route Documentation"

    A single route can be assigned to a specific OpenAPI specification by setting the flag to the name of the specification.

    ```kotlin
    get("example", {
        specName = "version1" //(1)!
    }) { /*...*/ }
    ```
    
    1. Assigns the route `/example` to a specification with name `version1`.
    
    This can also be done for a whole group of routes by adding it to a higher up route
    
    ```kotlin
    route("v1", {
        specName = "version1" //(1)!
    })  {
        get("example", {}) { /*...*/ }
        post("example", {}) { /*...*/ }
        delete("example", {}) { /*...*/ }
    }
    ```
    
    1. Assigns the route `/v1` and all child routes to a specification with name `version1`.

=== "Plugin Configuration"

    Routes can be assigned to specific OpenAPI specifications via an "assigner"-function in the plugin configuration
    that takes the url and tags of a route and returns the name of the specification to assign that route to.
    This function will only be used for routes that are not already assigned to a specification by other means. 
    
    ```kotlin
    install(OpenApi) {
        specAssigner = { url, tags -> url.firstOrNull() ?: "default" } //(1)!
    }
    ```
    
    1. Assigns all (still unassigned) routes to the specification with the name equal to the first part in the url or the route (or "default" for root-routes).
    
    
    ## Serving Multiple OpenAPI Specifications
    
    Routes providing the different specifications have to be created for each one.
    
    ```kotlin
    routing {
        route("v1/api.json") {
            openApi("version1") //(1)!
        }
        route("v2/api.json") {
            openApi("version2") //(2)!
        }
    }
    ```
    
    1. Provide the OpenAPI specification with name `version1` at `/v1/api.json`
    2. Provide the OpenAPI specification with name `version2` at `/v2/api.json`

### Combine with Swagger UI and ReDoc

When using the [Swagger UI](../swaggerui/index.md) or [ReDoc](../redoc/index.md) library, handling multiple OpenAPI specifications is straight forward.

=== "Swagger UI (multiple)"
    Multiple OpenAPI specifications can be served in multiple "instances" of Swagger UI

    ```kotlin
    routing {
        route("v1") {
            route("api.json") {
                openApi("version1") //(1)!
            }
            route("swagger") {
                swaggerUI("/v1/api.json") //(2)!
            }
        }
        route("v2") {
            route("api.json") {
                openApi("version2") //(3)!
            }
            route("swagger") {
                swaggerUI("/v2/api.json") //(4)!
            }
        }
    }
    ```

    1. Provide OpenAPI specification with name `version1` at `/v1/api.json`
    2. Serve Swagger UI at `/v1/swagger` with specification from `/v1/api.json`
    3. Provide OpenAPI specification with name `version2` at `/v2/api.json`
    4. Serve Swagger UI at `/v2/swagger` with specification from `/v2/api.json`

=== "Swagger UI (single)"
    Multiple OpenAPI specifications can be served in the same Swagger UI and browsed via a dropdown selection.

    ```kotlin
    routing {
        route("v1/api.json") {
            openApi("version1") //(1)!
        }
        route("v2/api.json") {
            openApi("version2") //(2)!
        }
        route("swagger") {
            swaggerUI(mapOf( //(3)!
                "Version 1" to "/v1/api.json",
                "Version 2" to "/v2/api.json",
            ))
        }
    }
    ```

    1. Provide the OpenAPI specification with name `version1` at `/v1/api.json`
    2. Provide the OpenAPI specification with name `version2` at `/v2/api.json`
    3. Serve a Swagger UI at `/swagger` with both OpenAPI specification selectable. `Version 1` and `Version 2` are the names with which they are shown in the ui.

=== "ReDoc"
    Multiple OpenAPI specifications can be served in multiple "instances" of ReDoc

    ```kotlin
    routing {
        route("v1") {
            route("api.json") {
                openApi("version1") //(1)!
            }
            route("redoc") {
                redoc("/v1/api.json") //(2)!
            }
        }
        route("v2") {
            route("api.json") {
                openApi("version2") //(3)!
            }
            route("redoc") {
                redoc("/v2/api.json") //(4)!
            }
        }
    }
    ```

    1. Provide OpenAPI specification with name `version1` at `/v1/api.json`
    2. Serve ReDoc at `/v1/redoc` with specification from `/v1/api.json`
    3. Provide OpenAPI specification with name `version2` at `/v2/api.json`
    4. Serve ReDoc at `/v2/redoc` with specification from `/v2/api.json`