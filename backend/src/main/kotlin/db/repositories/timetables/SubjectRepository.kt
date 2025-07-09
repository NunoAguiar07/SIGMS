package isel.leic.group25.db.repositories.timetables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.db.repositories.utils.withDatabase
import isel.leic.group25.db.tables.Tables.Companion.subjects
import org.ktorm.database.Database
import org.ktorm.dsl.and
import org.ktorm.dsl.eq
import org.ktorm.dsl.like
import org.ktorm.entity.*

class SubjectRepository(private val database: Database): SubjectRepositoryInterface {
    override fun getAllSubjects(limit:Int, offset:Int): List<Subject> = withDatabase {
        return database.subjects.drop(offset).take(limit).toList()
    }

    override fun getAllSubjectsByUniversityId(universityId: Int, limit: Int, offset: Int): List<Subject> {
        return database.subjects.filter { it.university eq universityId }.drop(offset).take(limit).toList()
    }

    override fun getSubjectsByNameAndUniversityId(universityId: Int, subjectPartialName: String, limit: Int, offset: Int): List<Subject> {
        return database.subjects.filter {
            (it.university eq universityId) and
            (it.name like "%$subjectPartialName%")
        }.drop(offset).take(limit).toList()
    }


    override fun findSubjectById(id: Int): Subject? = withDatabase {
        return database.subjects.firstOrNull { it.id eq id }
    }

    override fun findSubjectByName(name: String): Subject? = withDatabase {
        return database.subjects.firstOrNull { it.name eq name }
    }

    override fun createSubject(name: String, university: University): Subject = withDatabase {
        val newSubject = Subject {
            this.name = name
            this.university = university
        }
        database.subjects.add(newSubject)
        return newSubject
    }

    override fun deleteSubject(id: Int): Boolean = withDatabase {
        val condition = database.subjects.removeIf { it.id eq id }
        return condition > 0
    }
}