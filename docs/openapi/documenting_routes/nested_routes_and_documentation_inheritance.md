# Nested Routes & Documentation Inheritance

Ktor routes can be nested to create hierarchical URL structures. The OpenAPI plugin extends this concept by allowing child routes to inherit documentation from parent routes, enabling shared configuration without repetition. Common properties like security requirements, tags, or parameters can be defined once on a parent route and automatically apply to all children.

Documentation can be added at any level, and child routes automatically inherit documentation from their parents.

When a route is processed, documentation from all parent routes is collected and merged with the route's own documentation. The merging behavior depends on the type of property.

```kotlin
route("api", {
    tags = listOf("api")
}) {
    route("users", {
        description = "User management operations"
    }) {
        get({
            description = "List all users"
        }) {
            // Effective tags: ["api"]
            // Description: "List all users"
        }
    }
}
```