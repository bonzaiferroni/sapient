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
import java.util.Date

fun Application.configureRoutes() {
    routing {
        get(v1) {
            call.respondText("Hello World!")
        }

        // applyServiceRouting(AreaService())
        // applyServiceRouting(EventService())
        // applyServiceRouting(UserService())

        post("$v1/login") {
            val audience = "http://localhost:8080/"
            val issuer = "http://localhost:8080/"
            val secret = "secret"

            val json = call.receive<JsonObject>()
            val name = json["name"]?.jsonPrimitive?.content
            val password = json["password"]?.jsonPrimitive?.content
            if (name != "admin" || password != "admin") {
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