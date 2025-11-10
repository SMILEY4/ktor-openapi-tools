# Troubleshooting & FAQ


## Plugin Configuration

### Rate Limit Plugin Routes Appearing Incorrectly

Routes wrapped with `rateLimit` appear as `/(RateLimit test)/endpoint` instead of `/endpoint`.

**Possible Solution**

Add the rate limit route selector to the ignored list:

```kotlin
install(OpenApi) {
    ignoredRouteSelectors += RateLimitRouteSelector::class
}
```

??? info "More Information"

    [:octicons-arrow-right-24: Plugin Configuration - Ignored Route Selectors](./plugin_configuration.md/#ignored-route-selectors)




### Resources Plugin Not Installed Error

Error when using type-safe routing: `Application plugin Resources is not installed`.

**Possible Solution**

The OpenAPI plugin doesn't automatically install the Resources plugin.

Install the Resources plugin explicitly before the OpenAPI plugin:

```kotlin
install(Resources) // (1)!
install(OpenApi) {
    autoDocumentResourcesRoutes = true
}
```

1. Must be installed separately

??? info "More Information"

    [:octicons-arrow-right-24: Type-Safe Routing Support](./advanced_topics/typesafe_routing_support.md)




## Schema Generation


### Swagger Annotations Not Appearing in Generated Schemas

Using `@Schema` annotations from Swagger with `SchemaGenerator.kotlinx()`, but descriptions and other annotation values don't appear in the generated specification.

**Possible Solution**

Swagger annotations are not supported by kotlinx.serialization due to how annotations are processed during compilation. Use the matching annotations from schema-kenerator-core instead:

```kotlin
import io.github.smiley4.schemakenerator.core.annotations.Description

@Serializable
data class User(
    @Description("Unique user identifier")
    val id: String,
    @Description("User's display name")
    val name: String
)
```

??? info "More Information"

    [:octicons-arrow-right-24: Automatic Schema Generation - Kotlinx.Serialization-Based Generation](./working_with_schemas/automatic_schema_generation_overview.md#kotlinxserialization-based-generation)



### Schema Overwrites Not Taking Effect with Kotlinx.Serialization

Configured a SchemaOverwriteModule but the custom schema doesn't appear in the specification.

**Possible Solution**

The identifier in the overwrite module must match the serial descriptor name used by your custom serializer, not necessarily the qualified class name.

Ensure the identifier matches your serializer's descriptor name:

```kotlin
// Custom serializer
object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = 
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING) // (1)!
}

// Overwrite module - identifier must match
object InstantOverwrite : SchemaOverwriteModule(
    identifier = "Instant", // (2)!
    schema = {
        Schema<Any>().also {
            it.types = setOf("string")
            it.format = "date-time"
        }
    }
)
```

1. Serial name defined in custom serializer
2. Identifier must match the serial name exactly

??? info "More Information"

    [:octicons-arrow-right-24: Automatic Schema Generation - Kotlinx.Serialization-Based Generation](./working_with_schemas/automatic_schema_generation_overview.md#kotlinxserialization-based-generation)




## Authentication and Security

### Cookie Authentication Not Working in Swagger UI

Cookie authentication configured but "Try it out" doesn't include the Cookie header.

**Possible Solution**

This is a limitation of Swagger UI, not the plugin. Swagger UI does not support cookie authentication in its "Try it out" feature.

Reference: [Swagger UI Cookie Authentication Documentation](https://swagger.io/docs/specification/v3_0/authentication/cookie-authentication/)