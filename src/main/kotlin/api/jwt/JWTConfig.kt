package isel.leic.group25.api.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class JwtConfig(private val secret: String,
                private val issuer: String,
                private val audience: String,
                private val realm: String
) {
    private val algorithm = Algorithm.HMAC256(secret)

    fun generateToken(username: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withClaim("username", username)
            .sign(algorithm)
    }
}