package isel.leic.group25.api.http.routes

import api.model.response.WelcomePageResponse
import io.ktor.server.routing.*
import io.ktor.server.response.*

fun Route.welcomeRoutes() {
    get {
        call.respond(
            WelcomePageResponse(
                title = "Welcome to SIGMS",
                description = "An application to manage classes and schedules",
                version = "0.0.1"
            )
        )
    }
}