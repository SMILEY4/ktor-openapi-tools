# Customizing Schema Generation

The default schema generator can be replaced and completely customized in the schema section of the plugin config.
Schemas are automatically generated from types using [schema-kenerator](https://github.com/SMILEY4/schema-kenerator).

```kotlin
schemas {
    generator = { type ->
        // return a `io.github.smiley4.schemakenerator.swagger.data.CompiledSwaggerSchema`
        // for the given type here
    }
}
```

???+ example

    ```kotlin
    schemas {
        generator = { type ->
            type
                .processReflection()
                .generateSwaggerSchema()
                .withTitle(TitleType.SIMPLE)
                .compileReferencingRoot()
        }
    }
    ```

!!! info
    More information can be found in the [wiki of the schema-kenerator project](https://github.com/SMILEY4/schema-kenerator/wiki) together with an overview of the additional required dependencies.
