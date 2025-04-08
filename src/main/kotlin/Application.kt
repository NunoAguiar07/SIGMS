package isel.leic.group25

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import isel.leic.group25.api.http.configureRouting
import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.UserService
import org.ktorm.database.Database

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    val db = Database.connect(url = "jdbc:postgresql://db:5432/sigms", user = "user", password = "password123", driver = "org.postgresql.Driver")
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    val jwtConfig = JwtConfig(secret, issuer, audience, myRealm)
    val kTransaction = KTransaction(db)
    val userRepository = UserRepository(db)
    val classRepository = ClassRepository(db)
    val classService = ClassService(classRepository, kTransaction)
    val userService = UserService(userRepository, kTransaction, jwtConfig)

    install(Authentication) {
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                jwtConfig.buildVerifier()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token expired or invalid")
            }
        }
    }
    configureRouting(userService, classService)
}
