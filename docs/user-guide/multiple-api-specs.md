# Multiple API Specs

## Assigning routes to a specific OpenAPI spec
Routes can be assigned in two ways

### Assign routes with flags
A single route can be assigned to a specific openapi spec by setting the flag to the name or id of the spec.

```kotlin
get("example", {
    specName = "version1"
}) {
    //...
}
```

This can also be done for a whole group of routes by adding it to a higher up `route`

```kotlin
route("v1", {
	specName = "version1" // assigns all child routes to the spec "version1"
})  {
    
    get("example", {}) { /*...*/ }
    post("example", {}) { /*...*/ }
    delete("example", {}) { /*...*/ }
    
}
```

### Assign routes with spec-assigner
The spec-assigner in the plugin configuration is given the url (as a list of parts) and the tags of all routes and provides the name/id of an openapi spec to assign the route to.
This will only be done for routes that don't have a spec assigned already via a flag.

```kotlin
install(OpenApi) {
    //...
    specAssigner = { url, tags -> url.firstOrNull() ?: "default" }
}
```

## Configuring routing for api spec files, Swagger UI and ReDoc

Routes for the spec-file, Swagger UI and ReDoc have to be configured manually for each spec.

```kotlin
routing {
    // add routes for "version1" spec, swagger ui and redoc
    route("v1") {
    	route("api.json") {
            // api spec containing all routes assigned to "version1"
            openApi("version1")
        }
        route("swagger") {
            // swagger-ui using '/v1/api.json'
            swaggerUI("/v1/api.json")
        }
        route("redoc") {
            // redoc using '/v1/api.json'
            redoc("/v1/api.json")
        }
    }

    // add routes for "version2" spec
    route("v2") {
    	route("api.json") {
            openApi("version2")
        }
        route("swagger") {
            // swagger-ui using '/v2/api.json'
            swaggerUI("/v2/api.json")
        }
        route("redoc") {
            // redoc using '/v2/api.json'
            swaggerUI("/v2/api.json")
        }
    }

}
```

## Configuring individual OpenAPI Specs
Every individual OpenAPI spec, Swagger UI and ReDoc can be fully customized.
The normal plugin config serves as a base for all specs, which can then be overwritten.

```kotlin
install(OpenApi) {
    // "global" configuration for all specs
    info {
        title = "Example API"
    }

    // configuration specific to "version1" spec. Overwrites global config
    spec("version1") {
        info {
            version = "1.0"
        }
    }

    // configuration specific to "version2" spec. Overwrites global config
    spec("version2") {
        info {
            version = "2.0"
        }
    }
}
```
