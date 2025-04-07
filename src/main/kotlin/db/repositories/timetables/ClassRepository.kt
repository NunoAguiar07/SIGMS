package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import org.ktorm.database.Database
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.tables.Tables.Companion.classes
import isel.leic.group25.db.tables.Tables.Companion.studentsClasses
import isel.leic.group25.db.tables.Tables.Companion.teachersClasses
import kotlinx.datetime.Instant
import org.ktorm.dsl.eq
import org.ktorm.entity.*


class ClassRepository(private val database: Database): ClassRepositoryInterface {
    override fun findClassById(id: Int): Class? {
        return database.classes.firstOrNull { it.id eq id }
    }

    override fun findClassesBySubject(subject: Subject): List<Class> {
        return database.classes.toList().filter { it.subject == subject }
    }

    override fun findClassesByType(type: ClassType): List<Class> {
        return database.classes.toList().filter { it.type == type }
    }

    override fun findClassesInTimeRange(start: Instant, end: Instant): List<Class> {
        return database.classes.toList().filter {
            (it.startTime >= start && it.startTime < end) ||
                    (it.endTime > start && it.endTime <= end) ||
                    (it.startTime <= start && it.endTime >= end)
        }
    }

    override fun findClassesOverlappingWith(classToCheck: Class): List<Class> {
        return database.classes.toList().filter {
            it.id != classToCheck.id &&
                    ((it.startTime >= classToCheck.startTime && it.startTime < classToCheck.endTime) ||
                            (it.endTime > classToCheck.startTime && it.endTime <= classToCheck.endTime) ||
                            (it.startTime <= classToCheck.startTime && it.endTime >= classToCheck.endTime))
        }
    }

    override fun addClass(newClass: Class): Boolean {
        return if (database.classes.none { it.id eq newClass.id }) {
            database.classes.add(newClass)
            true
        } else {
            false
        }
    }

    override fun updateClass(updatedClass: Class): Boolean {
        val index = database.classes.toList().indexOfFirst { it.id == updatedClass.id }
        return if (index >= 0) {
           database.classes.filter { it.id eq updatedClass.id }
                .forEach {
                    it.subject = updatedClass.subject
                    it.type = updatedClass.type
                    it.startTime = updatedClass.startTime
                    it.endTime = updatedClass.endTime
                }
            true
        } else {
            false
        }
    }

    override fun deleteClass(id: Int): Boolean {
        return database.classes.removeIf {  it.id eq id } > 0
    }

    override fun findClassesByStudentId(userId: Int): List<Class> {
        return database.studentsClasses.filter { it.userId eq userId }
            .map { it.schoolClass }
    }

    override fun findClassesByTeacherId(userId: Int): List<Class> {
        return database.teachersClasses.filter { it.userId eq userId }
            .map { it.schoolClass }
    }
}