package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.subjects
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.*

class SubjectRepository(private val database: Database): SubjectRepositoryInterface {
    override fun getAllSubjects(limit:Int, offset:Int): List<Subject> {
        return database.subjects.drop(offset).take(limit).toList()
    }

    override fun findSubjectById(id: Int): Subject? {
        return database.subjects.firstOrNull { it.id eq id }
    }

    override fun findSubjectByName(name: String): Subject? {
        return database.subjects.firstOrNull { it.name eq name }
    }

    override fun createSubject(name: String): Subject {
        val newSubject = Subject {
            this.name = name
        }
        database.subjects.add(newSubject)
        return newSubject
    }
}