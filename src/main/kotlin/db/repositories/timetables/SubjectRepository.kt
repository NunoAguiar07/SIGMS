package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.db.tables.Tables.Companion.subjects
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.add
import org.ktorm.entity.firstOrNull
import org.ktorm.entity.toList

class SubjectRepository(private val database: Database): SubjectRepositoryInterface {
    override fun getAllSubjects(): List<Subject> {
        return database.subjects.toList()
    }

    override fun findSubjectById(id: Int): Subject? {
        return database.subjects.firstOrNull { it.id eq id }
    }

    override fun findSubjectByName(name: String): Subject? {
        return database.subjects.firstOrNull { it.name eq name }
    }

    override fun createSubject(name: String): Subject? {
        val newSubject = Subject {
            this.name = name
        }
        database.subjects.add(newSubject)
        return findSubjectByName(name)
    }
}