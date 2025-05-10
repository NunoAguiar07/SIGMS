package mocks.repositories.timetables

import UniversityRepositoryInterface
import isel.leic.group25.db.entities.timetables.University

class MockUniversityRepository : UniversityRepositoryInterface {
    private val universities = mutableListOf<University>()

    override fun getAllUniversities(limit: Int, offset: Int): List<University> {
        return universities.drop(offset).take(limit).toList()
    }

    override fun getUniversityById(id: Int): University? {
        return universities.firstOrNull { it.id == id }
    }

    override fun getUniversityByName(name: String): University? {
        return universities.firstOrNull { it.name == name }
    }

    override fun createUniversity(name: String): University {
        val university = University {
            this.name = name
        }
        universities.add(university)
        return university
    }

    fun clear() {
        universities.clear()
    }
}