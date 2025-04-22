package isel.leic.group25.services.email.model

data class EmailConfig(
    val host: String,
    val port: Int,
    val username: String,
    val password: String,
    val from: String,
    val useSsl: Boolean = true,
    val baseUrl: String
)