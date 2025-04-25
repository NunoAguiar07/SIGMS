package mocks.repositories.users

import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.StudentRepositoryInterface

class MockStudentRepository : StudentRepositoryInterface {
    private val students = mutableListOf<Student>()
    private val mockUsers = mutableListOf<User>()


    fun addMockStudent(user: User): Student {
        val student = Student { this.user = user }
        students.add(student)
        mockUsers.add(user)
        return student
    }

    override fun findStudentById(id: Int): Student? {
        return students.firstOrNull { it.user.id == id }
    }

    override fun findStudentByEmail(email: String): Student? {
        val user = mockUsers.firstOrNull { it.email == email } ?: return null
        return students.firstOrNull { it.user.id == user.id }
    }

    override fun isStudent(user: User): Boolean {
        return students.any { it.user.id == user.id }
    }

    fun clear() {
        students.clear()
        mockUsers.clear()
    }
}