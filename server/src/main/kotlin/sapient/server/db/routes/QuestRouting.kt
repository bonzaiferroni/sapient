package sapient.server.db.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import sapient.model.quest.Quest
import sapient.server.db.services.QuestService
import sapient.server.plugins.v1

fun Routing.provideCustomRoutes(questService: QuestService) {
    get("$v1/quest/roots") {
        val roots = questService.getRoots()
        call.respond(HttpStatusCode.OK, roots)
    }

    get("$v1/quest/{id}/children") {
        val id = call.parameters["id"]?.toIntOrNull()
        if (id == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid id")
            return@get
        }
        val children = questService.getChildren(id)
        call.respond(HttpStatusCode.OK, children)
    }

    get("$v1/quest/available") {
        val available = questService.getAvailable()
        call.respond(HttpStatusCode.OK, available)
    }

    get("$v1/quest/completed") {
        val start = call.parameters["start"]?.toLongOrNull() ?: 0
        val end = call.parameters["end"]?.toLongOrNull() ?: Long.MAX_VALUE
        val completed = questService.getCompleted(start, end)
        call.respond(HttpStatusCode.OK, completed)
    }
}