# Ktor Swagger UI

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.smiley4/ktor-swagger-ui/badge.svg)](https://search.maven.org/artifact/io.github.smiley4/ktor-swagger-ui)
[![Checks Passing](https://github.com/SMILEY4/ktor-swagger-ui/actions/workflows/checks.yml/badge.svg?branch=develop)](https://github.com/SMILEY4/ktor-swagger-ui/actions/workflows/checks.yml)

This library provides a [Ktor](https://ktor.io/) plugin to document routes, generate an OpenAPI specification and serve [Swagger UI](https://swagger.io/tools/swagger-ui/) and [ReDoc](https://github.com/Redocly/redoc).

It is meant to be  minimally invasive i.e. it can be plugged into existing application without requiring immediate changes to the code.
Routes can then be gradually enhanced with documentation.


## Features

- Minimally invasive (no immediate change to existing code required)
- Provides OpenAPI spec, Swagger UI and ReDoc with minimal configuration
- Supports most of the [OpenAPI 3.1.0 Specification](https://swagger.io/specification/)
- Automatic [json-schema generation](https://github.com/SMILEY4/schema-kenerator) from arbitrary types/classes for bodies and parameters
    - Supports generics, inheritance, collections, etc.
    - (Optional) support for Jackson-annotations and swagger schema annotations
    - Use with reflection or kotlinx-serialization
    - Customizable schema generation
