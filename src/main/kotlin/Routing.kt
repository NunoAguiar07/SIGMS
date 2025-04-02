package isel.leic.group25

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.model.Link
import isel.leic.group25.api.model.WelcomePageResponse

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(
                WelcomePageResponse(
                    title = "Welcome to SIGMS",
                    description = "An application to manage classes and schedules",
                    version = "0.0.1",
                    links = listOf(
                        Link("login", "/auth/login"),
                        Link("signup", "/auth/signup"),
                    )
                )
            )
        }

    }
}
