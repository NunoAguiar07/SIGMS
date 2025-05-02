package isel.leic.group25.api.jwt

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getUserIdFromPrincipal(): Int? {
    return principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asInt()
}

fun ApplicationCall.getUserRoleFromPrincipal(): String? {
    return principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()
}