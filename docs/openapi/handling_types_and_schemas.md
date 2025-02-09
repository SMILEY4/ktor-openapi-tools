# Handling Types and Schemas


## Local Types and Schemas

Usually, schemas for request and response bodies, headers and parameters are specified directly at the route and then automatically collected during generation of the OpenAPI specification.

There are three main ways of specifying the schema of any object:

=== "Type Parameter"
    The type can be specified via a type parameter.</br>The schema is then generated automatically.
    ```kotlin
    body<ExampleData> { /*...*/ }
    ```
    ```kotlin
    queryParameter<String>("example") { /*...*/ }
    ```
    ```kotlin
    headerParameter<List<String>>("X-Example") { /*...*/ }
    ```
    etc

=== "KType"
    The type can be passed as a parameter of type KType.</br>The schema is then generated automatically.
    ```kotlin
    body(typeof<ExampleData>) { /*...*/ }
    ```
    ```kotlin
    queryParameter("example", typeof<String>) { /*...*/ }
    ```
    ```kotlin
    headerParameter("X-Example", typeof<List<String>>) { /*...*/ }
    ```
    etc

=== "Swagger Schema"
    The type can be passed as a parameter of type `io.swagger.v3.oas.models.media.Schema`.</br>The given schema is then used as is.
    ```kotlin
    body(Schema<Any>().apply {
        types = setOf("object")
        title = "ExampleData"
        //...
    }) { /*...*/ }
    ```
    ```kotlin
    queryParameter(
        "example",
        Schema<Any>().apply {
            types = setOf("string")
            //...
        }
    ) { /*...*/ }
    ```
    ```kotlin
    headerParameter(
        "X-Example",
        Schema<Any>().apply {
            types = setOf("string")
            //...
        }
    ) { /*...*/ }
    ```
    etc

## Global Types and Schemas

In addition to types and schema specified directly at the routes, global schemas can be defined in the schema section of the plugin configuration that can then be used and shared by any route documentation. The ids of the schemas must be unique.

???+ example "Defining Global Schemas"

    ```kotlin
    install(OpenApi) {
        schemas {
            
            schema<ExampleData>("example-data") //(1)!
            
            schema("string", typeOf<String>()) //(2)!
            
            schema("integer", Schema<Any>().apply { //(3)!
                types = setOf("number")
                format = "int32"
            })
            
        }
    }
    ```
    
    1. Define a new global schema with id `example-data` via the type parameter of `schema`. The actual schema for the type is generated automatically.
    2. Define a new global schema with id `string` by passing a `KType` to `schema`. The actual schema for the type is generated automatically.
    3. Define a new global schema with id `integer` by passing a `io.swagger.v3.oas.models.media.Schema` to `schema`.

Global schemas can be referenced by any route documentation by their schema id using the `ref`-function

???+ example "Referencing Global Schemas"

    ```kotlin
    routing {

        get({
            request {
                queryParameter("limit", ref("integer")) //(1)!
                headerParameter("X-Custom", ref("string")) //(2)!
                body(ref("example-1")) //(3)!
            }
        }) {
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

    }
    ```
    
    1. Use the global schema with id `integer` as the schema for the query parameter.
    2. Use the global schema with id `string` as the schema for the header parameter.
    3. Use the global schema with id `example-1` as the request body schema.


## Composite Schemas