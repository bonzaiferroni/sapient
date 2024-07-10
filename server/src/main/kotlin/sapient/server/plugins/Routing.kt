package sapient.server.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import sapient.model.misc.User
import sapient.server.db.routes.provideBasicRoutes
import sapient.server.db.routes.provideCustomRoutes
import sapient.server.db.services.MissionService
import sapient.server.db.services.QuestService
import java.util.Date

fun Application.configureRoutes() {
    routing {
        get(v1) {
            call.respondText("Hello World!")
        }

        provideBasicRoutes(QuestService())
        provideBasicRoutes(MissionService())

        provideCustomRoutes(QuestService())

        post("$v1/login") {
            val audience = "http://localhost:8080/"
            val issuer = "http://localhost:8080/"
            val secret = "secret"

            val user = call.receive<User>()
            if (user.username != "admin" || user.password != "admin") {
                call.respond(HttpStatusCode.Unauthorized, "Invalid user")
                return@post
            }

            val token = JWT.create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withExpiresAt(Date(System.currentTimeMillis() + 60000))
                // .withClaim("username", user.name)
                .sign(Algorithm.HMAC256(secret))
            call.respond(hashMapOf("token" to token))
        }
    }
}

val v1 = "/api/v1"