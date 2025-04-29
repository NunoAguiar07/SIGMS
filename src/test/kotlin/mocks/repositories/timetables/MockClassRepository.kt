package mocks.repositories.timetables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface

class MockClassRepository : ClassRepositoryInterface {
    private val classes = mutableListOf<Class>()
    private val studentsClasses = mutableListOf<Attend>()
    private val teachersClasses = mutableListOf<Teach>()

    override fun findAllClasses(): List<Class> = classes.toList()

    override fun findClassById(id: Int): Class? = classes.firstOrNull { it.id == id }

    override fun findClassByName(name: String): Class? = classes.firstOrNull { it.name == name }

    override fun findClassesBySubject(subject: Subject, limit: Int, offset: Int): List<Class> {
        return classes.filter { it.subject.id == subject.id }.drop(offset).take(limit)
    }

    override fun addClass(name: String, subject: Subject): Class {
        val newClass = Class {
            this.name = name
            this.subject = subject
        }
        classes.add(newClass)
        return newClass
    }

    override fun updateClass(updatedClass: Class): Boolean {
        val index = classes.indexOfFirst { it.id == updatedClass.id }
        return if (index != -1) {
            classes[index] = updatedClass
            true
        } else false
    }

    override fun deleteClassById(id: Int): Boolean {
        return classes.removeIf { it.id == id }
    }

    override fun deleteClass(toBeDeletedClass: Class): Boolean {
        return classes.removeIf { it.id == toBeDeletedClass.id }
    }

    override fun addStudentToClass(student: Student, schoolClass: Class): Boolean {
        if (checkStudentInClass(student.user.id, schoolClass.id)) return false
        studentsClasses.add(Attend().apply {
            this.student = student
            this.schoolClass = schoolClass
        })
        return true
    }

    override fun removeStudentFromClass(user: User, schoolClass: Class): Boolean {
        return studentsClasses.removeIf { it.student.user.id == user.id && it.schoolClass.id == schoolClass.id }
    }

    override fun addTeacherToClass(teacher: Teacher, schoolClass: Class): Boolean {
        if (checkTeacherInClass(teacher.user.id, schoolClass.id)) return false
        teachersClasses.add(Teach().apply {
            this.teacher = teacher
            this.schoolClass = schoolClass
        })
        return true
    }

    override fun removeTeacherFromClass(user: User, schoolClass: Class): Boolean {
        return teachersClasses.removeIf { it.teacher.user.id == user.id && it.schoolClass.id == schoolClass.id }
    }

    override fun checkStudentInClass(userId: Int, classId: Int): Boolean {
        return studentsClasses.any { it.student.user.id == userId && it.schoolClass.id == classId }
    }

    override fun checkTeacherInClass(userId: Int, classId: Int): Boolean {
        return teachersClasses.any { it.teacher.user.id == userId && it.schoolClass.id == classId }
    }

    override fun findClassesByStudentId(userId: Int): List<Class> {
        return studentsClasses.filter { it.student.user.id == userId }.map { it.schoolClass }
    }

    override fun findClassesByTeacherId(userId: Int): List<Class> {
        return teachersClasses.filter { it.teacher.user.id == userId }.map { it.schoolClass }
    }

    override fun findStudentsByClassId(classId: Int): List<Student> {
        return studentsClasses.filter { it.schoolClass.id == classId}.map { it.student }
    }

    override fun findTeachersByClassId(classId: Int): List<Teacher> {
        return teachersClasses.filter { it.schoolClass.id == classId}.map { it.teacher }
    }

    fun clearAll() {
        classes.clear()
        studentsClasses.clear()
        teachersClasses.clear()
    }
}
