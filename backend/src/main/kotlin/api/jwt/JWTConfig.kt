package isel.leic.group25.api.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtConfig(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val realm: String,
    private val accessTokenExpiration: Long = 3600L * 1000L, // 1 hour
    private val refreshTokenExpiration: Long = 30L * 24L * 3600L * 1000L // 30 days
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateAccessToken(userId: Int, role: String? = null, universityId: Int? = null): String {
        return JWT.create()
            .withSubject("access")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withClaim("universityId", universityId)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiration))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: Int): String {
        return JWT.create()
            .withSubject("refresh")
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .sign(algorithm)
    }

    fun buildAccessTokenVerifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withSubject("access")
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }

    fun buildRefreshTokenVerifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withSubject("refresh")
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
}