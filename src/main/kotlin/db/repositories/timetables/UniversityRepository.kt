package isel.leic.group25.db.repositories.timetables

import UniversityRepositoryInterface
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.tables.Tables.Companion.universities
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.like
import org.ktorm.entity.*

class UniversityRepository(private val database: Database): UniversityRepositoryInterface {
    override fun getAllUniversities(limit:Int, offset:Int): List<University> {
        return database.universities.drop(offset).take(limit).toList()
    }

    override fun getUniversityById(id: Int): University? {
        return database.universities.firstOrNull { it.id eq id }
    }

    override fun getUniversityByName(name: String): University? {
        return database.universities.firstOrNull { it.name eq name }
    }

    override fun getUniversitiesByName(limit: Int, offset: Int, subjectPartialName: String): List<University> {
        return database.universities
            .filter { (it.name like "%$subjectPartialName%") }
            .drop(offset).take(limit).toList()
    }

    override fun createUniversity(name: String): University {
        val university = University {
            this.name = name
        }
        database.universities.add(university)
        return university
    }

}