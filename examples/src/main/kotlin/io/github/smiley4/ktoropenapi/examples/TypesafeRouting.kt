package io.github.smiley4.ktoropenapi.examples

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktoropenapi.resources.delete
import io.github.smiley4.ktoropenapi.resources.get
import io.github.smiley4.ktoropenapi.resources.post
import io.github.smiley4.ktorredoc.redoc
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.github.smiley4.schemakenerator.serialization.SerializationSteps.analyzeTypeUsingKotlinxSerialization
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.compileReferencingRoot
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.generateSwaggerSchema
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.handleCoreAnnotations
import io.github.smiley4.schemakenerator.swagger.SwaggerSteps.withTitle
import io.github.smiley4.schemakenerator.swagger.data.TitleType
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.resources.Resources
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun main() {
    embeddedServer(Netty, port = 8080, host = "localhost", module = Application::myModule).start(wait = true)
}

private fun Application.myModule() {

    install(Resources)

    install(OpenApi) {
        // enable automatically extracting documentation from resources-routes
        autoDocumentResourcesRoutes = true
        // schema-generator must use kotlinx-serialization to be compatible with resources
        schemas {
            generator = { type ->
                type
                    .analyzeTypeUsingKotlinxSerialization()
                    .generateSwaggerSchema()
                    .withTitle(TitleType.SIMPLE)
                    .handleCoreAnnotations()
                    .compileReferencingRoot()
            }
        }
    }

    routing {

        // add the routes for the api-spec, swagger-ui and redoc
        route("api.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("redoc") {
            redoc("/api.json")
        }

        // query and path parameters are picked up automatically and added to the schema with the correct name and type
        get<PetsRoute.All> { request ->
            println("..${request.tags}, ${request.limit}")
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        // additional information can be added to the route manually as usual.
        // automatically added information can also be overwritten this way.
        get<PetsRoute.Id>({
            request {
                pathParameter<Long>("id") {
                    description = "the id of the pet"
                }
            }
        }) { request ->
            println("..${request.id}")
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        // delete route
        delete<PetsRoute.Id.Delete> { request ->
            println("..${request.parent.id}")
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

        // post with request body. Request body is added to the schema automatically.
        post<PetsRoute.Id.New, Pet> { request, body ->
            println("..${request.parent.id} $body")
            call.respond(HttpStatusCode.NotImplemented, Unit)
        }

    }

}


@Resource("/pets")
class PetsRoute {

    @Resource("/")
    class All(
        val parent: PetsRoute,
        val tags: List<String>?,
        val limit: Int = 100
    )


    @Resource("{id}")
    class Id(
        val parent: PetsRoute,
        val id: Long
    ) {

        @Resource("/")
        class Delete(
            val parent: Id
        )


        @Resource("/")
        class New(
            val parent: Id,
        )

    }

}


@Serializable
data class Pet(
    val name: String,
    val tag: String
)