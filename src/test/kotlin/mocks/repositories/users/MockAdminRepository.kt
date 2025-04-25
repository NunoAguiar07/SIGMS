package mocks.repositories.users

import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.AdminRepositoryInterface

class MockAdminRepository(private val userRepository: MockUserRepository) : AdminRepositoryInterface {
    private val admins = mutableListOf<Admin>()

    override fun getAllAdminEmails(): List<String> = admins.map { it.user.email }

    override fun findAdminById(id: Int): Admin? = admins.firstOrNull { it.user.id == id }

    override fun findAdminByEmail(email: String): Admin? {
        val user = userRepository.findByEmail(email) ?: return null
        return admins.firstOrNull { it.user.id == user.id }
    }

    override fun isAdmin(user: User): Boolean = admins.any { it.user.id == user.id }
}