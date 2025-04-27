package isel.leic.group25.api.http.utils

import io.ktor.server.routing.*
import isel.leic.group25.db.entities.types.Role

fun Route.withRole(role: Role, build: Route.() -> Unit): Route {
    return withRoles(setOf(role), build)
}

fun Route.withRoles(roles: Set<Role>, build: Route.() -> Unit): Route {
    val newRoute = createChild(object : RouteSelector() {
        override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int) =
            RouteSelectorEvaluation.Constant
    })
    newRoute.install(RoleAuthorization) {
        this.roles = roles
    }
    newRoute.build()
    return newRoute
}