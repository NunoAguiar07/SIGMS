package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.Teacher
import isel.leic.group25.db.entities.users.User


interface ClassRepositoryInterface {
    fun findAllClasses(): List<Class>

    fun findClassById(id: Int): Class?

    fun findClassByName(name: String): Class?

    fun findClassesBySubject(subject: Subject, limit:Int, offset:Int): List<Class>

    fun addClass(name: String, subject: Subject): Class

    fun updateClass(updatedClass: Class): Boolean

    fun deleteClassById(id: Int): Boolean

    fun deleteClass(toBeDeletedClass: Class): Boolean

    fun addStudentToClass(student: Student, schoolClass: Class): Boolean

    fun removeStudentFromClass(user: User, schoolClass: Class): Boolean

    fun addTeacherToClass(teacher: Teacher, schoolClass: Class): Boolean

    fun removeTeacherFromClass(user: User, schoolClass: Class): Boolean

    fun checkStudentInClass(userId: Int, classId:Int): Boolean

    fun checkStudentInSubject(userId: Int, subjectId: Int): Boolean

    fun checkTeacherInClass(userId: Int, classId: Int): Boolean

    fun findClassesByStudentId(userId: Int): List<Class>

    fun findClassesByTeacherId(userId: Int): List<Class>

    fun findStudentsByClassId(classId: Int): List<Student>

    fun findTeachersByClassId(classId: Int): List<Teacher>
}