package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User

interface TeacherRepositoryInterface {
    fun findTeacherById(id: Int): Teacher?

    fun findTeacherByEmail(email: String): Teacher?

    fun isTeacher(user: User): Boolean

    fun findTeachersByClassId(classId: Int): List<Teacher>
}