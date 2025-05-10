import isel.leic.group25.db.entities.timetables.University


interface UniversityRepositoryInterface {
    fun getAllUniversities(limit:Int, offset:Int): List<University>
    fun getUniversityByName(name: String): University?
    fun getUniversityById(id: Int): University?
    fun createUniversity(name: String): University
}