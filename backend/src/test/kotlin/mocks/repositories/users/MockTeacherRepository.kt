package mocks.repositories.users

import isel.leic.group25.db.entities.users.Teach
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase

class MockTeacherRepository : TeacherRepositoryInterface {
    private val teachers = mutableListOf<Teacher>()
    private val mockUsers = mutableListOf<User>()
    private val teaches = mutableListOf<Teach>()


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

    override fun findTeachersByClassId(classId: Int): List<Teacher> {
        val teachers = withDatabase {
            teaches.filter { it.schoolClass.id == classId }
        }
        return  withDatabase {
            teachers.map { it.teacher }
        }
    }
    override fun universityTeachers(universityId: Int): List<Teacher> {
        return teachers.filter { it.user.university.id == universityId }
    }

    fun clear() {
        teachers.clear()
        mockUsers.clear()
    }
}