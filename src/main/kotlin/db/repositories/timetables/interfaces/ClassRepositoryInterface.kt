package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.users.User

// still need to remove some methods after some time
interface ClassRepositoryInterface {
    fun findAllClasses(): List<Class>

    fun findClassById(id: Int): Class?

    fun findClassByName(name: String): Class?

    fun findClassesBySubject(subject: Subject, limit:Int, offset:Int): List<Class>

    fun addClass(name: String, subject: Subject): Class

    fun updateClass(updatedClass: Class): Boolean

    fun deleteClassById(id: Int): Boolean

    fun deleteClass(toBeDeletedClass: Class): Boolean

    fun addStudentToClass(user: User, schoolClass: Class): Boolean

    fun removeStudentFromClass(user: User, schoolClass: Class): Boolean

    fun findClassesByStudentId(userId: Int): List<Class>

    fun findClassesByTeacherId(userId: Int): List<Class>
}