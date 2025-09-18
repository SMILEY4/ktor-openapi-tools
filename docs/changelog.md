---
search:
  exclude: true
---

# Changelog

## 5.3.0

- fix kotlinx-serialization example encoding [#212](https://github.com/SMILEY4/ktor-openapi-tools/issues/212)
- fix missing response bodies from documented resources routes [#209](https://github.com/SMILEY4/ktor-openapi-tools/issues/209)
- upgrade schema-kenerator from 2.3.0 to [2.4.0](https://github.com/SMILEY4/schema-kenerator/releases/tag/2.4.0)
- upgrade ktor from 3.1.1 to ktor 3.2.3

## 5.2.0

- upgrade schema-kenerator from 2.1.3 to [2.3.0](https://github.com/SMILEY4/schema-kenerator/releases/tag/2.3.0)
- fix handling of different OpenApi specs with multiple ktor modules

## 5.1.0

- upgrade schema-kenerator to [2.1.3](https://github.com/SMILEY4/schema-kenerator/releases/tag/2.1.3)
- add OpenApi extension support for route documentation [#202](https://github.com/SMILEY4/ktor-openapi-tools/pull/202)
- add `docExpansion` property to SwaggerUI configuration controlling the default expansion setting for the operations and tags [#191](https://github.com/SMILEY4/ktor-openapi-tools/pull/191)

## 5.0.2

- upgrade schema-kenerator to [2.1.2](https://github.com/SMILEY4/schema-kenerator/releases/tag/2.1.2)
- fix incorrect schema for multipart bodies

## 5.0.1

- upgrade schema-kenerator to [2.1.1](https://github.com/SMILEY4/schema-kenerator/releases/tag/2.1.1)
- fixed bug: routes requiring authorization sometimes not automatically detected as "protected" ([#186](https://github.com/SMILEY4/ktor-openapi-tools/issues/186))
- simplify configuration of json naming strategy with pre-built kotlinx schema generators

## 5.0.0

- split project into submodules: ktor-openapi and ktor-swagger-ui (and ktor-redoc)
    - "ktor-openapi" plugin is responsible only for generating and providing an openapi-file
        - no longer `install(SwaggerUI)` but now `install(OpenApi)`
        - can be used without the swagger-ui dependency
    - "ktor-swagger" library to provide routes to server swagger-ui for any openapi-file (based on given url)
        - can be used with any openapi file or generator
    - this split allows for more flexibility and new features using openapi-files without swagger-ui (e.g. redoc)

- cleanup package structure, naming, configuration
    - shorter and cleaner package names
    - shorter and more uniform class names
    - *slightly* simpler configuration dsl
    - made all internal classes actually "internal" -> less namespace pollution

- TypesafeRouting plugin
    - improved support for TypesafeRouting plugin
    - automatically detect path and query parameters

- improved schema generation
    - added typealias `GenericSchemaGenerator` for schema generation function
    - create pre-defined configurable schema generators for reflection and kotlinx-serialization
    - kotlinx-serialization schema generator can be configured using the kotlinx "Json" object (also used for serializing real ktor requests and responses)
    - added pre-defined custom analysis and schema generation modules (SchemaGenerator.TypeOverwrites.XYZ) for common types
    - remove old "type overwrites" (no longer necessary due to more powerful and flexible schema generator)

- example encoding
    - renamed typealias `ExampleEncoder` to `GenericExampleEncoder`
    - moved default example encoder to `ExampleEncoder.internal`
    - moved `kotlinxExampleEncoder` to ExampleEncoder.kotlinx
    - create pre-defined configurable example encoders for internal swagger encoder and kotlinx-serialization
    - kotlinx-serialization example encoder can be configured using the kotlinx "Json" object (also used for serializing real ktor requests and responses)

- added support for ReDoc as new library

- added missing configuration options for Swagger UI

- added basic support for documenting webhooks

- upgrade schema-kenerator to 2.1.0

- upgrade ktor to 3.1.1

- overhauled documentation

## 4.1.7

- fixed bug: routes requiring authorization sometimes not automatically detected as "protected" ([#186](https://github.com/SMILEY4/ktor-openapi-tools/issues/186))

## 4.1.6

- fixed bug: schemas of headers in multipart bodies were not generated correctly and caused exceptions
- added `required` property to multipart parts

## 4.1.5

- fix bug: plugin configuration not merged correctly when using multiple openapi-specs (some changes in main config did
  not apply to specific spec configs)
- fix bug: multipart-body used the old schema `type` property instead of types, resulting in a broken schema
- fixed examples: multipart-body examples used the old schema `type` property instead of types, resulting in a broken
  schemas

## 4.1.4

- fixed: root path set via `application.rootPath = "..."` was ignored in openapi

## 4.1.3

- upgrade schema-kenerator from 1.6.2 to [1.6.3](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.6.3)

## 4.1.2

- upgrade schema-kenerator from 1.6.1 to [1.6.2](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.6.2)

## 4.1.1

- upgrade schema `io.swagger.parser.v3:swagger-parser` from 2.1.20 to 2.1.24
- upgrade schema-kenerator from 1.6.0 to [1.6.1](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.6.1)

## 4.1.0

- add default kotlinx example encoder
- add `ignoredRouteSelectorClassNames` in case class to ignore is not
  public ([#149](https://github.com/SMILEY4/ktor-swagger-ui/pull/149))
- upgrade schema-kenerator from 1.5.0 to [1.6.0](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.6.0)
- add `@KtorDsl` annotation on documented routes
- fixed: default security schema did not correctly use components section
- fixed documented resource routes ([#153](https://github.com/SMILEY4/ktor-swagger-ui/pull/153))

## 4.0.0

- upgrade ktor to 3.0.0 ([#140](https://github.com/SMILEY4/ktor-swagger-ui/pull/140))
- removed unnecessary ktor Webjars plugin

## 3.6.1

- upgraded schema-kenerator from 1.5.0 to [1.5.1](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.5.1)

## 3.6.0

- support location "cookies" for request parameters
- option to serve api-spec as yaml instead of json
     ```kotlin
     install(SwaggerUI) {
         outputFormat = OutputFormat.YAML
     }
     ```
- fix bug: additional slashes in routes caused parts of url being dropped
- upgrade schema kenerator from 1.4.3 to [1.5.0](https://github.com/SMILEY4/schema-kenerator/releases/tag/1.5.0)
- bump versions of some dependencies

## 3.5.1

- properly render openapi-spec as 3.1.0
- upgrade schema-kenerator from 1.4.1 to 1.4.3
- upgrade ktor from 2.3.11 to 2.3.12

## 3.5.0

- upgrade schema-kenerator to version 1.4.1
- fixed: incorrect http-status-code format in responses object
- fixed: "ktor.deployment.rootPath" is included in paths

## 3.4.0

- upgrade schema-kenerator to version 1.2.2
- removed some openapi fields with default values in the generated spec that are usually optional (this resulted in some
  invalid schemas before)

## 3.3.1

- upgrade schema-kenerator to version 1.1.1

## 3.3.0

- upgrade schema-kenerator from 1.0.1 to 1.1.0
- path-parameters are not "required" by default
- fixed issue with config merging, resulting in some plugin config changes to be ignored or having no effect

## 3.2.0

- fixed bug: security examples not generated with correct type
- quality of life improvements to dsl

## 3.1.0

- add option to customize schema encoding

## 3.0.1

- fixed body of defaultUnauthorizedResponse
- upgraded schema-kenerator from 1.0.0 to 1.0.1

## 3.0.0

- improved default schema-generator configuration
- improved documentation

**Changes from [3.0.0-beta1](https://github.com/SMILEY4/ktor-swagger-ui/releases/tag/3.0.0-beta1)**

- reworked schema and example handling; replaced previously used json schema generator
  with https://github.com/SMILEY4/schema-kenerator
  *this switch should result in more predictable behavior, cleaner schemas in the spec and allow for more flexibility
  and better support for future features and bugfixes specific to the ktor-swagger-ui plugin*

- dropped automatic swagger-ui routing. Routes for swagger-ui and the openapi-file have to be added manually at the
  desired url
  *this change should make the routing of the ui and spec more visible and intuitive while also allowing for easier
  customization of the routes*

- cleaned up plugin-configuration and openapi-dsl
  *removed no longer necessary configuration and cleaned up existing groups making the plugin config a bit cleaner*

- examples are no longer part of the tests and have been moved to an own subproject. Reworked examples to better
  showcase functionalities.

**Changes from [3.0.0-beta2](https://github.com/SMILEY4/ktor-swagger-ui/releases/tag/3.0.0-beta2)**

- improved (added back) proper dsl for schemas and examples
- upgraded to schema-kenerator version 0.2.0 (https://github.com/SMILEY4/schema-kenerator/releases/tag/0.2.0)

**Changes from [3.0.0-beta3](https://github.com/SMILEY4/ktor-swagger-ui/releases/tag/3.0.0-beta3)**

- add option to disable syntax highlighting (`SwaggerUiSyntaxHighlight#DISABLED`)
- upgrade schema-kenerator to version 0.4.0 (https://github.com/SMILEY4/schema-kenerator/releases/tag/0.4.0)
- uprade swagger-ui webjars to version 5.17.11
- upgrade to openapi version 3.1.0 and add support for more properties
    - added `info.summary`
    - added `license.identifier`
    - added `server.variables`
    - added `header.explode`
    - added `parameter.style`
    - added `route.externalDocs`, `route.servers`
- fix: swagger routing config

## 2.10.1

- fixed bug: routes with same "url" not added to spec when rootHost-path is configured

## 2.10.0

- [ktor.deployment.rootPath](https://ktor.io/docs/server-configuration-file.html#predefined-properties) is appended to
  paths in openapi.json

## 2.9.0

- added option to manually configure routing for swagger-ui and api-spec-json
- added (back) jackson-module - results in jackson annotation and sub-types being detected automatically again

## 2.8.0

- add hook whenBuildOpenApiSpecs to process/customize final api-content after openapi-generation generation
- add withCredentials (see https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration)

## 2.7.5

- bump victools/jsonschema-generator to 4.33+ and include Option.INLINE_NULLABLE_SCHEMAS

## 2.7.4

- fix bug: incorrect handling of rootHostPath

## 2.7.3

- change name of AuthScheme#BEARER from "Bearer" to "bearer"
- bump ktor version to 2.3.7
- bump swagger-parser version to 2.1.19

## 2.7.2

- fix bug: invalid property "name" in security schemes

## 2.7.1

- fix: displayed routes using the resources-plugin contained parameters in the url

## 2.7.0

- upgraded dependency versions
- fixed: missing properties that start with 'is[A-Z]' https://github.com/SMILEY4/ktor-swagger-ui/issues/60

## 2.6.0

- allow for multiple openapi-specs and swagger-uis
- simpler request and response-body schema customization (oneOf, multipleOf, custom)

## 2.5.0

- add support for type-safe routes / resources-plugin

## 2.4.0

- add support for external documentation

## 2.3.1

- fix bug: schemas with wildcard generics (e.g. Array<\*>) throw exception

## 2.3.0

- add `rootHostPath` to plugin-config for improved support for reverse-proxies
- add `customSchemas.includeAll` to plugin-config to also include custom schemas that are not directly references by any
  route
- add github actions to automatically run tests and-code style-checks

## 2.2.3

- fix bug: schema for file-uploads not generated correctly

## 2.2.2

- fix bug: Schema-annotation with "nullable = true" breaks schema generation

## 2.2.1

- fixed a bug where the component name of schemas of inner array elements was wrong

## 2.2.0

- publish to maven central

## 2.1.0

- add flag `protected` to route-documentation to overwrite default behaviour, i.e. manually mark route as protected or
  not

## 2.0.0

- improved automatic schema handling
- changes in plugin-configuration
    - removed `canonicalNameObjectRefs`, `schemasInComponentSection`, `examplesInComponentSection`, `jsonSchemaBuilder`
    - replaced `automaticTagGenerator` with `generateTags`
    - added `encoding`-section
- schema and example generation process more extendable (via `encoding`-config section) allowing for simpler integration
  of other schema-generators and json-serializers (e.g. supporting kotlinx and multiplatform) -
  see [KotlinxExample.kt](https://github.com/SMILEY4/ktor-swagger-ui/blob/develop/src/test/kotlin/io/github/smiley4/ktorswaggerui/examples/KotlinxExample.kt)
- print exceptions thrown during api generation

## 1.6.1

- upgrade swagger parser to 2.1.13 due to vulnerabilities

## 1.6.0

- add `@Example`-Annotation to add example values directly on fields of the model
- support for (some features of) the openapi-core `@Schema`-Annotation

## 1.5.0

- added "hidden"-flag to not include routes in openapi-spec

## 1.4.0

- add (basic) support for swagger @Schema-annotation to add more information (mainly description, title, required,
  nullable) to schemas/fields

## 1.3.2

- fix bug: primitive body types (int, float, ...) default to type "string" when specified via generics

## 1.3.1

- fix bug: example values for parameters could not be set and were mixed up with the "explode"-option

## 1.3.0

- allow multiple security schemes per route

## 1.2.0

- add config (`ignoredRouteSelectors`) to ignore specific ktor route-selectors
- enable usage of custom object schemas as object or array
- add option to mark routes as deprecated
- bugfix: changed `canonicalNameObjectRefs` in plugin-config from val to var

## 1.1.1

- fix bug: changed canonicalNameObjectRefs to var in plugin-config

## 1.1.0

- use simpleName instead of canonicalName for objects by default, can be configured via `canonicalNameObjectRefs`
- upgrade to ktor 2.2.2
- bugfix: fixed "operationsSorter"-value

## 1.0.2

- fix bug: json-schemas for maps were not generated correctly

## 1.0.1

- exclude documented-route from route-string

## 1.0.0

🥳

## 0.9.0

- make json-schema-generator configurable
- upgrade to ktor 2.2.1

## 0.8.0

- add (basic) support for multipart-bodies

## 0.7.0

- add additional configuration-options for swagger-ui
- fix: add missing enum mapping for schemas

## 0.6.0

- added support for custom predefined schemas
- added support for custom json-schema-builders

## 0.5.2

- fixed bug: configured rootPath (in application.conf) not respected

## 0.5.1

- fixed bug: security scheme not added to openapi-spec

## 0.5.0

- add `@DslMarker` for more safety
- protect Swagger-UI and OpenApi-json with custom authentication
- improved/proper support for generics in schemas (e.g. `body<MyResponse>()` as alternative to
  `body(MyResponse::class)`)
- support external json-schemas for bodies
- support for default responses and responses for custom status-codes
- support `operationId`

## 0.4.0

- add oauth flows

## 0.3.0

- support examples for request and response bodies

## 0.2.0

- replace java with kclass

## 0.1.0

- prepare for publishing
