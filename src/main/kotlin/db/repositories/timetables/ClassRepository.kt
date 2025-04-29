package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import org.ktorm.database.Database
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.users.*
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.studentsClasses
import isel.leic.group25.db.tables.Tables.Companion.teachersClasses
import org.ktorm.dsl.*
import org.ktorm.entity.*


class ClassRepository(private val database: Database): ClassRepositoryInterface {
    override fun findAllClasses(): List<Class> {
        return database.classes.toList()
    }
    override fun findClassById(id: Int): Class? {
        return database.classes.firstOrNull { it.id eq id }
    }

    override fun findClassByName(name: String): Class? {
        return database.classes.firstOrNull { it.name eq name }
    }

    override fun findClassesBySubject(subject: Subject, limit:Int, offset:Int): List<Class> {
        return database.classes.filter { it.subject eq subject.id }.drop(offset).take(limit).toList()
    }

    override fun addClass(name: String, subject: Subject): Class {
        val newClass = Class {
            this.name = name
            this.subject = subject
        }
        database.classes.add(newClass)
        return newClass
    }

    override fun updateClass(updatedClass: Class): Boolean {
        return updatedClass.flushChanges() > 0
    }

    override fun deleteClassById(id: Int): Boolean {
        return database.classes.removeIf { (it.id eq id) } > 0
    }

    override fun deleteClass(toBeDeletedClass: Class): Boolean {
        return toBeDeletedClass.delete() > 0
    }

    override fun addStudentToClass(student: Student, schoolClass: Class): Boolean {
        return database.studentsClasses.add(Attend {
                this.student = student
                this.schoolClass = schoolClass
            }) > 0
    }

    override fun removeStudentFromClass(user: User, schoolClass: Class): Boolean {
        return database.studentsClasses.removeIf { (it.studentId eq user.id) and (it.classId eq schoolClass.id) } > 0
    }

    override fun addTeacherToClass(teacher: Teacher, schoolClass: Class): Boolean {
        return database.teachersClasses.add(Teach{
                this.teacher = teacher
                this.schoolClass = schoolClass
        }) > 0
    }

    override fun removeTeacherFromClass(user: User, schoolClass: Class): Boolean {
        return database.teachersClasses.removeIf { (it.teacherId eq user.id) and (it.classId eq schoolClass.id) } > 0
    }

    override fun checkStudentInClass(userId: Int, classId: Int): Boolean {
        return database.studentsClasses.any { (it.studentId eq userId) and (it.classId eq classId) }
    }

    override fun checkTeacherInClass(userId: Int, classId: Int): Boolean {
        return database.teachersClasses.any { (it.teacherId eq userId) and (it.classId eq classId) }
    }

    override fun findClassesByStudentId(userId: Int): List<Class> {
        return database.studentsClasses.filter { it.studentId eq userId }
            .map { it.schoolClass }
    }

    override fun findClassesByTeacherId(userId: Int): List<Class> {
        return database.teachersClasses.filter { it.teacherId eq userId }.map { it.schoolClass }
    }

    override fun findStudentsByClassId(classId: Int): List<Student> {
        return database.studentsClasses.filter { it.classId eq classId }.map { it.student }
    }


    override fun findTeachersByClassId(classId: Int): List<Teacher> {
        return database.teachersClasses.filter { it.classId eq classId }.map { it.teacher }
    }

}