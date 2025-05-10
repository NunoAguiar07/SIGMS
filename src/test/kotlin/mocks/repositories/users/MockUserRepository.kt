package mocks.repositories.users

import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface

class MockUserRepository : UserRepositoryInterface {
    private val users = mutableListOf<User>()
    private val admins = mutableListOf<Admin>()
    private val students = mutableListOf<Student>()
    private val teachers = mutableListOf<Teacher>()
    private val technicalServices = mutableListOf<TechnicalService>()

    override fun findById(id: Int): User? = users.firstOrNull { it.id == id }

    override fun findByEmail(email: String): User? = users.firstOrNull { it.email == email }

    override fun associateWithRole(newUser: User, role: Role): User {
        when(role) {
            Role.ADMIN -> admins.add(Admin { user = newUser })
            Role.STUDENT -> students.add(Student { user = newUser })
            Role.TEACHER -> teachers.add(Teacher { user = newUser })
            Role.TECHNICAL_SERVICE -> technicalServices.add(TechnicalService { user = newUser })
        }
        return newUser
    }


    override fun createWithRole(email: String, username: String, password: String, role: Role, university: University): User {
        val newUser = User {
            this.email = email
            this.username = username
            this.password = User.hashPassword(password)
            this.university = university
        }
        users.add(newUser)
        return associateWithRole(newUser, role)
    }

    override fun createWithoutRole(email: String, username: String, password: String, university: University): User {
        val newUser = User {
            this.email = email
            this.username = username
            this.password = User.hashPassword(password)
            this.university = university
        }
        users.add(newUser)
        return newUser
    }

    override fun update(user: User): Int {
        val index = users.indexOfFirst { it.id == user.id }
        return if (index >= 0) {
            users[index] = user
            1
        } else {
            0
        }
    }

    override fun getRoleById(id: Int): Role? {
        return when {
            admins.any { it.user.id == id } -> Role.ADMIN
            students.any { it.user.id == id } -> Role.STUDENT
            teachers.any { it.user.id == id } -> Role.TEACHER
            technicalServices.any { it.user.id == id } -> Role.TECHNICAL_SERVICE
            else -> null
        }
    }

    override fun delete(id: Int): Boolean {
        users.firstOrNull { it.id == id } ?: return false
        admins.removeIf { it.user.id == id }
        students.removeIf { it.user.id == id }
        teachers.removeIf { it.user.id == id }
        technicalServices.removeIf { it.user.id == id }
        users.removeIf { it.id == id }
        return true
    }

    fun clear() {
        users.clear()
        admins.clear()
        students.clear()
        teachers.clear()
        technicalServices.clear()
    }
}

