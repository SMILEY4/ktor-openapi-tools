# Types and Schemas

## Specifying Types and Schemas Directly
Usually, schemas for request and response bodies, headers and parameters are generated automatically from types specified directly at the route.

### As generic types

```kotlin
body<MyExampleData>(/*...*/)

queryParameter<String>(/*...*/)

header<List<String>>(/*...*/) 

//...
```

### As KTypes

```kotlin
body(typeof<MyExampleData>(), /*...*/)

queryParameter(typeof<String>(), /*...*/)

header(typeof<List<String>>(), /*...*/)

//...
```

### As a Swagger Schema
```kotlin
body(Schema<Any>().also {
    it.type = "object"
    //...
}, /*...*/)

queryParameter(Schema<Any>().also {
    it.type = "string"
}, /*...*/)

header(Schema<Any>().also {
    it.type = "array"
    //...
}, /*...*/)

//...
```

## Global Types and Schemas
In addition, **custom** and **global** schemas can be defined in the schema section of the plugin configuration and then be referenced by multiple routes by their schema ids.

### Defining global schemas

```kotlin
install(OpenApi) {
    schemas {
        // register new schema with the id "example-schema-1" and a generic type
        schema<MyExampleData("example-schema-1")

        // register new schema with the id "example-schema-2" and a KType
        schema("example-schema-2", typeof<String>())

        // register new schema with the id "example-schema-3" and a swagger-schema
        schema("example-schema-3", Schema<Any>().also {
            it.type = "array"
            //...
        })
    }
}
```

### Referencing global schemas

```kotlin
import io.github.smiley4.ktoropenapi.config.ref

//...

body(ref("example-schema-1"), /*...*/)

queryParameter(ref("example-schema-2"), /*...*/)

header(ref("example-schema-2"), /*...*/)

//...
```


## Composite Schemas
Schemas and types can be combined to created variations without having to create new classes.
Available additional composite operations are `array` and `anyOf`

### array
Creates a schema of an array containing items of the specified type or schema.

```kotlin
import io.github.smiley4.ktoropenapi.config.array

//... 

body(array<MyExampleData>())

//...
```

### anyOf
Creates a schema that describes any one of the specified types or schemas.

```kotlin
import io.github.smiley4.ktoropenapi.config.anyOf

//...

body(anyOf(typeOf<MyExampleData>(), typeOf<MyOtherData>() /*, ...*/))

//...
```

### nesting
The operations array and anyOf as well as ref and type can be combined and nested to create more complex variations.

```kotlin
body(
    anyOf(
        array<String>(),
        anyOf(
            ref("my-schema"),
            type<MyExampleData>()
        ),        
    )
)
```

## Overwriting Types
Types can be overwritten with own provided schemas or other types.

```kotlin
install(OpenApi) {
    schemas {
        // overwrite type "List<Int>" with an array of either floats or integer 
        overwrite<List<Int>(
            array(
                anyOf(
                    typeof<Float>(),
                    typeof<Int>()
                )
            )
        ), 

        // overwrite type "File" with custom schema for binary data
        overwrite<File>(Schema<Any>().also {
            it.type = "string"
            it.format = "binary"
        })

    }
}
```

!!! note
    When a body or parameter in a request or response defines a schema of a type that is overwritten in the config, the other given type or schema will be used instead.

!!! warning
    Overwriting only work for top-level types and not nested structures (e.g. fields in other types).
    See [Customizing Schema Generation](./customizing-schema-generation.md) and [schema-kenerator Type Redirects](https://github.com/SMILEY4/schema-kenerator/wiki/Type-Redirects) instead.
