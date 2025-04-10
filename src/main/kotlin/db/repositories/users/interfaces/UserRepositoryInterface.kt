package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.*

interface UserRepositoryInterface {
    fun findById(id: Int): User?

    fun findByEmail(email: String): User?

    fun create(newUser: User, role: Role): User

    fun update(user: User): Int

    fun getRoleById(id: Int): Role?
}