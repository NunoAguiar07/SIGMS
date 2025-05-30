package isel.leic.group25.api.http

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import isel.leic.group25.api.http.routes.authRoutes
import isel.leic.group25.api.http.routes.authenticatedRoutes
import isel.leic.group25.api.http.routes.universityRoutes
import isel.leic.group25.api.http.routes.welcomeRoutes
import isel.leic.group25.services.*
import isel.leic.group25.websockets.hardware.route.DeviceRoute

fun Application.configureRouting(
    services: Services,
    client:HttpClient
) {
    routing {
        route("/api") {
            welcomeRoutes()
            universityRoutes(services)
            authRoutes(services, client)
            authenticatedRoutes(
                services
            )
        }
        with(DeviceRoute){
            install()
        }
    }

}