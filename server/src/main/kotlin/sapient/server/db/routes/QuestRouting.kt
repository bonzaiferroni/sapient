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
    get("/$v1/quest/roots") {
        val roots = questService.getRoots()
        if (roots.isEmpty()) {
            call.respond(HttpStatusCode.OK, emptyList<Quest>())
        } else {
            call.respond(HttpStatusCode.OK, roots)
        }
    }
}