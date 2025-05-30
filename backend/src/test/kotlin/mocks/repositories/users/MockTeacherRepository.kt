package mocks.repositories.users

import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface

class MockTeacherRepository : TeacherRepositoryInterface {
    private val teachers = mutableListOf<Teacher>()
    private val mockUsers = mutableListOf<User>()

    // Test setup helper
    fun addMockTeacher(user: User): Teacher {
        val teacher = Teacher { this.user = user }
        teachers.add(teacher)
        mockUsers.add(user)
        return teacher
    }

    override fun findTeacherById(id: Int): Teacher? {
        return teachers.firstOrNull { it.user.id == id }
    }

    override fun findTeacherByEmail(email: String): Teacher? {
        val user = mockUsers.firstOrNull { it.email == email } ?: return null
        return teachers.firstOrNull { it.user.id == user.id }
    }

    override fun isTeacher(user: User): Boolean {
        return teachers.any { it.user.id == user.id }
    }

    fun clear() {
        teachers.clear()
        mockUsers.clear()
    }
}