package isel.leic.group25.model

data class TechnicalServices(
    override val id: Int,
    override val email: String,
    override val name: String,
    override val password: String,
    override val profileImage: String?
) : User(id, email, name, password, profileImage)
