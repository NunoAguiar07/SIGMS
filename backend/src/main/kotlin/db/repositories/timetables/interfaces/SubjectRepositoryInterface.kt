package isel.leic.group25.db.repositories.timetables.interfaces

import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.timetables.University

interface SubjectRepositoryInterface {
    fun getAllSubjects(limit:Int, offset:Int): List<Subject>
    fun getAllSubjectsByUniversityId(universityId: Int, limit:Int, offset:Int): List<Subject>
    fun getSubjectsByNameAndUniversityId(universityId: Int, subjectPartialName: String, limit:Int, offset:Int): List<Subject>
    fun findSubjectById(id: Int): Subject?
    fun findSubjectByName(name: String): Subject?
    fun createSubject(name: String, university: University): Subject
    fun deleteSubject(id: Int): Boolean
}