package mocks.repositories.timetables

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface

class MockSubjectRepository : SubjectRepositoryInterface {

    private val subjects = mutableListOf<Subject>()
    private var nextSubjectId = 1  // Define this at the class level

    override fun getAllSubjects(limit: Int, offset: Int): List<Subject> {
        return subjects.drop(offset).take(limit)
    }

    override fun getAllSubjectsByUniversityId(universityId: Int, limit: Int, offset: Int): List<Subject> {
        return subjects.filter { it.university.id == universityId }.drop(offset).take(limit)
    }

    override fun getSubjectsByNameAndUniversityId(universityId: Int, subjectPartialName: String, limit: Int, offset: Int): List<Subject> {
        return subjects.filter {
            it.university.id == universityId &&
            it.name.contains(subjectPartialName, ignoreCase = true)
        }.drop(offset).take(limit)
    }

    override fun findSubjectById(id: Int): Subject? {
        return subjects.firstOrNull { it.id == id }
    }

    override fun findSubjectByName(name: String): Subject? {
        return subjects.firstOrNull { it.name == name }
    }



    override fun createSubject(name: String, university: University): Subject {
        val newSubject = Subject {
            this.id = nextSubjectId++            // Assign unique ID
            this.name = name
            this.university = university
        }
        subjects.add(newSubject)
        return newSubject
    }

    override fun deleteSubject(id: Int): Boolean {
        return subjects.removeIf { it.id == id }
    }

}