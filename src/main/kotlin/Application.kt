package isel.leic.group25

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.jetty.jakarta.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import isel.leic.group25.api.http.configureRouting
import isel.leic.group25.api.jwt.JwtConfig
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.services.*
import kotlinx.serialization.json.Json
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import kotlin.time.Duration.Companion.seconds

val applicationHttpClient = HttpClient(OkHttp) {
    install(io.ktor.client.plugins.contentnegotiation.ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(CORS) {
        //allowSameOrigin
        allowHost("localhost:8081", schemes = listOf("http")) // Expo front end
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)

        // Allow headers
        allowHeader(HttpHeaders.Cookie)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("X-Device")

        allowCredentials = true

        // Max age for preflight requests
        maxAgeInSeconds = 24 * 60 * 60
    }
    install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
        }
        )
    }

    val db = Database.connect(
        url = System.getenv("DB_URL"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD"),
        driver = "org.postgresql.Driver",
        dialect = PostgreSqlDialect()
    )



    val secret = System.getenv("JWT_SECRET")
    val issuer = System.getenv("JWT_ISSUER")
    val audience = System.getenv("JWT_AUDIENCE")
    val myRealm = System.getenv("JWT_REALM")

    val jwtConfig = JwtConfig(secret, issuer, audience, myRealm)


    install(Authentication) {
        oauth("auth-microsoft") {
            client = applicationHttpClient
            urlProvider = { "${System.getenv("FRONTEND_URL")}/auth/microsoft/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "microsoft",
                    authorizeUrl = "https://login.microsoftonline.com/${System.getenv("MICROSOFT_TENANT_ID")}/oauth2/v2.0/authorize",
                    accessTokenUrl = "https://login.microsoftonline.com/${System.getenv("MICROSOFT_TENANT_ID")}/oauth2/v2.0/token",
                    requestMethod = HttpMethod.Post,
                    clientId = System.getenv("MICROSOFT_CLIENT_ID"),
                    clientSecret = System.getenv("MICROSOFT_CLIENT_SECRET"),
                    defaultScopes = listOf("openid", "profile", "email"),
                    extraAuthParameters = listOf(
                        "response_type" to "code",
                        "prompt" to "select_account"
                    )
                )
            }
        }
        jwt("auth-jwt") {
            realm = myRealm
            verifier(
                jwtConfig.buildVerifier()
            )
            authHeader { call ->
                val authHeader = call.request.parseAuthorizationHeader()
                if (authHeader != null) {
                    return@authHeader authHeader
                }
                val token = call.request.cookies["auth_token"]
                if (token != null) {
                    return@authHeader HttpAuthHeader.Single("Bearer", token)
                }
                return@authHeader null
            }
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asInt()
                val role = credential.payload.getClaim("role").asString()
                val universityId = credential.payload.getClaim("universityId").asInt()

                if (userId != null && role != null && universityId != null) {
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
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 30.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    val repositories = Repositories(db)
    val services = Services(repositories, jwtConfig)

    configureRouting(
        services,
        applicationHttpClient
    )
}
