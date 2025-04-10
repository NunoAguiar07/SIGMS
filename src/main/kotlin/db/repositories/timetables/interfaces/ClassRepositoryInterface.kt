package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.users.User
import kotlinx.datetime.Instant

// still need to remove some methods after some time
interface ClassRepositoryInterface {
    fun findClassById(id: Int): Class?

    fun findClassesBySubject(subject: Subject): List<Class>

    fun findClassesByType(type: ClassType): List<Class>


    fun findClassesInTimeRange(start: Instant, end: Instant): List<Class> // kotlin datetime, see if it is correct

    fun findClassesOverlappingWith(classToCheck: Class): List<Class>

    fun addClass(newClass: Class): Boolean

    fun updateClass(updatedClass: Class): Boolean

    fun deleteClassById(id: Int): Boolean

    fun deleteClass(toBeDeletedClass: Class): Boolean

    fun addStudentToClass(user: User, schoolClass: Class): Boolean

    fun removeStudentFromClass(user: User, schoolClass: Class): Boolean

    fun findClassesByStudentId(userId: Int): List<Class>

    fun findClassesByTeacherId(userId: Int): List<Class>
}