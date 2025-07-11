package isel.leic.group25.api.http.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.db.entities.types.Role

val RoleAuthorization = createRouteScopedPlugin(
    name = "RoleAuthorization",
    ::RoleAuthorizationConfig
) {
    on(AuthenticationChecked) { call ->
        val requiredRoles = pluginConfig.roles
        if (requiredRoles.isNotEmpty()) {
            val userRoleString = call.getUserRoleFromPrincipal()
            val userRole = userRoleString?.let { Role.fromValue(it.uppercase()) }
            if (userRole == null || userRole !in requiredRoles) {
                call.respond(HttpStatusCode.Forbidden, "Access forbidden for role $userRole")
                return@on
            }
        }
    }
}