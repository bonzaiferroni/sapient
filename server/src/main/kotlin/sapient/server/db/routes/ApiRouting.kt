package sapient.server.db.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.jetbrains.exposed.dao.IntEntity
import sapient.server.db.services.DataService
import sapient.server.db.getIdOrThrow
import sapient.server.plugins.v1

inline fun <reified Data : Any, DataEntity : IntEntity> Routing.provideBasicRoutes(
    service: DataService<Data, DataEntity>
) {
    get("$v1/${service.endpoint}") {
        val search = call.parameters["search"] ?: ""
        val count = call.parameters["limit"]?.toIntOrNull() ?: 10
        val data = if (search.isBlank()) {
            service.readAll()
        } else {
            service.search(service.getSearchOp(search), count)
        }
        call.respond(HttpStatusCode.OK, data)
    }

    get("$v1/${service.endpoint}/{id}") {
        val id = call.getIdOrThrow()
        val data = service.read(id)
        if (data != null) {
            call.respond(HttpStatusCode.OK, data)
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }

    authenticate("auth-jwt") {
        post("$v1/${service.endpoint}") {
            val data = call.receive<Data>()
            val id = service.create(data)
            call.respond(HttpStatusCode.Created, id)
        }

        put("$v1/${service.endpoint}/{id}") {
            val id = call.getIdOrThrow()
            val data = call.receive<Data>()
            service.update(id, data)
            call.respond(HttpStatusCode.OK)
        }

        delete("$v1/${service.endpoint}/{id}") {
            val id = call.getIdOrThrow()
            service.delete(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}