package isel.leic.group25.api.http.routes

import api.model.response.WelcomePageResponse
import io.ktor.server.routing.*
import io.ktor.server.response.*

/**
 * Defines the welcome route for the application.
 *
 * This function sets up a GET endpoint at the root path ("/") that returns
 * a welcome message with basic application information.
 *
 * @receiver Route The Ktor route to which this endpoint will be added
 */
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