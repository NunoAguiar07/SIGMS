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
    private val expirationTime: Long = 3600L * 1000L // 1 hour
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(userId: Int, role: String? = null, universityId: Int? = null): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("userId", userId)
            .withClaim("role", role)
            .withClaim("universityId", universityId)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expirationTime))
            .sign(algorithm)
    }

    fun buildVerifier(): JWTVerifier {
        return JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()
    }
}