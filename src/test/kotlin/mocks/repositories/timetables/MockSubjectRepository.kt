package mocks.repositories.timetables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface

class MockSubjectRepository : SubjectRepositoryInterface {

    private val subjects = mutableListOf<Subject>()

    override fun getAllSubjects(limit: Int, offset: Int): List<Subject> {
        return subjects.drop(offset).take(limit)
    }

    override fun findSubjectById(id: Int): Subject? {
        return subjects.firstOrNull { it.id == id }
    }

    override fun findSubjectByName(name: String): Subject? {
        return subjects.firstOrNull { it.name == name }
    }

    override fun createSubject(name: String): Subject {
        val newSubject = Subject {
            this.name = name
        }
        subjects.add(newSubject)
        return newSubject
    }

    override fun deleteSubject(id: Int): Boolean {
        return subjects.removeIf { it.id == id }
    }

}