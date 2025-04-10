package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.timetables.Subject

interface SubjectRepositoryInterface {
    fun getAllSubjects(): List<Subject>
    fun findSubjectById(id: Int): Subject?
    fun findSubjectByName(name: String): Subject?
    fun createSubject(name: String): Subject?
}