package repositories.timetables

import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import repositories.DatabaseTestSetup
import kotlin.test.*

class UniversityRepositoryTest {
    private val kTormCommand = KtormCommand(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should create a new university and retrieve it by id`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("ISEL")
        val found = universityRepository.getUniversityById(university.id)

        assertNotNull(found)
        assertEquals(university.id, found.id)
        assertEquals("ISEL", found.name)
    }

    @Test
    fun `Should create a new university and retrieve it by name`() = kTormCommand.useTransaction {
        val university = universityRepository.createUniversity("IST")
        val found = universityRepository.getUniversityByName("IST")

        assertNotNull(found)
        assertEquals(university.id, found.id)
    }

    @Test
    fun `Should retrieve all universities`() = kTormCommand.useTransaction {
        val uni1 = universityRepository.createUniversity("UP")
        val uni2 = universityRepository.createUniversity("UA")

        val all = universityRepository.getAllUniversities(limit = 10, offset = 0)

        assertTrue(all.any { it.id == uni1.id })
        assertTrue(all.any { it.id == uni2.id })
    }

    @Test
    fun `Should retrieve universities by partial name`() = kTormCommand.useTransaction {
        universityRepository.createUniversity("University of Lisbon")
        universityRepository.createUniversity("Polytechnic Institute of Lisbon")
        universityRepository.createUniversity("University of Porto")

        val result = universityRepository.getUniversitiesByName(limit = 10, offset = 0, subjectPartialName = "Lisbon")

        assertEquals(2, result.size)
        assertTrue(result.all { it.name.contains("Lisbon") })
    }

    @Test
    fun `Should paginate universities correctly`() = kTormCommand.useTransaction {
        universityRepository.createUniversity("U1")
        universityRepository.createUniversity("U2")
        universityRepository.createUniversity("U3")

        val firstPage = universityRepository.getAllUniversities(limit = 2, offset = 0)
        val secondPage = universityRepository.getAllUniversities(limit = 2, offset = 2)

        assertEquals(2, firstPage.size)
        assertEquals(1, secondPage.size)
        assertNotEquals(firstPage[0].id, secondPage[0].id)
    }

    @Test
    fun `Should return null for non-existent university id`() = kTormCommand.useTransaction {
        val result = universityRepository.getUniversityById(999999)
        assertNull(result)
    }

    @Test
    fun `Should return null for non-existent university name`() = kTormCommand.useTransaction {
        val result = universityRepository.getUniversityByName("NonExistentUniversity")
        assertNull(result)
    }
}
