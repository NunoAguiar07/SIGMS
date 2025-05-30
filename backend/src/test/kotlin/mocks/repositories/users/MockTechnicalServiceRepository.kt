package mocks.repositories.users

import isel.leic.group25.db.entities.users.TechnicalService
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.TechnicalServiceRepositoryInterface

class MockTechnicalServiceRepository : TechnicalServiceRepositoryInterface {
    private val technicalServices = mutableListOf<TechnicalService>()
    private val mockUsers = mutableListOf<User>()

    override fun findTechnicalServiceById(id: Int): TechnicalService? {
        return technicalServices.firstOrNull { it.user.id == id }
    }

    override fun findTechnicalServiceByEmail(email: String): TechnicalService? {
        val user = mockUsers.firstOrNull { it.email == email } ?: return null
        return technicalServices.firstOrNull { it.user.id == user.id }
    }

    override fun isTechnicalService(user: User): Boolean {
        return technicalServices.any { it.user.id == user.id }
    }

    fun clear() {
        technicalServices.clear()
        mockUsers.clear()
    }
}