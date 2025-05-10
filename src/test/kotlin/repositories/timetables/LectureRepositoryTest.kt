package repositories.timetables

import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import repositories.DatabaseTestSetup
import kotlin.test.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class LectureRepositoryTest {
    private val kTransaction = KTransaction(DatabaseTestSetup.database)
    private val universityRepository = UniversityRepository(DatabaseTestSetup.database)
    private val lectureRepository = LectureRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val roomRepository = RoomRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
    }

    @Test
    fun `Should get lecture by ID with active change`() {
        kTransaction.useTransaction {
            val university = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Test Subject", university)
            val clazz = classRepository.addClass("Test Class", subject)
            val room = roomRepository.createRoom(20, "Test Room", university)
            val newRoom = roomRepository.createRoom(30, "New Test Room", university)
            roomRepository.createClassRoom(room)
            roomRepository.createClassRoom(newRoom)
            val classroom = roomRepository.getClassRoomById(room.id)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)
            assertNotNull(classroom)
            assertNotNull(newClassroom)

            val originalLecture = lectureRepository.createLecture(
                schoolClass = clazz,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )

            lectureRepository.updateLecture(
                originalLecture, newClassroom, ClassType.PRACTICAL, WeekDay.TUESDAY, 10.hours, 12.hours, 2
            )

            val foundLecture = lectureRepository.getLectureById(originalLecture.id)
            assertNotNull(foundLecture)
            assertEquals(WeekDay.TUESDAY, foundLecture.weekDay)
            assertEquals(10.hours, foundLecture.startTime)
            assertEquals(12.hours, foundLecture.endTime)
        }
    }


    @Test
    fun `Should get lecture by parameters`() {
        kTransaction.useTransaction {
            val university = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Test Subject", university)
            val clazz = classRepository.addClass("Test Class", subject)
            val room = roomRepository.createRoom(20, "Test Room", university)
            roomRepository.createClassRoom(room)
            val classroom = roomRepository.getClassRoomById(room.id)
            assertNotNull(classroom)

            val lecture = lectureRepository.createLecture(
                schoolClass = clazz,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )

            val foundLecture = lectureRepository.getLecture(
                schoolClass = clazz,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )
            assertNotNull(foundLecture)
            assertEquals(lecture, foundLecture)
        }
    }

    @Test
    fun `Should create a new lecture and find it by id`() {
        kTransaction.useTransaction {
            val subject1 = Subject {
                name = "test1"
            }
            val clazz = Class {
                name = "test1"
                subject = subject1
            }

            val room = Room {
                capacity = 20
                name = "testRoom"
            }
            val university = universityRepository.createUniversity("Test University")
            val newSubject = subjectRepository.createSubject(subject1.name, university)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity, room.name, university)
            roomRepository.createClassRoom(newRoom)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)
            assertNotNull(newClassroom)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                classroom = newClassroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getAllLectures().firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.classroom == newLecture.classroom)
            assert(foundLecture?.type == newLecture.type)
            assert(foundLecture?.weekDay == newLecture.weekDay)
            assert(foundLecture?.startTime == newLecture.startTime)
            assert(foundLecture?.endTime == newLecture.endTime)
        }
    }

    @Test
    fun `Should create a new lecture and find it by class id`() {
        kTransaction.useTransaction {
            val subject1 = Subject {
                name = "test2"
            }
            val clazz = Class {
                name = "test2"
                subject = subject1
            }

            val room = Room {
                capacity = 20
                name = "testRoom2"
            }
            val university = universityRepository.createUniversity("Test University")
            val newSubject = subjectRepository.createSubject(subject1.name, university)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name, university)
            roomRepository.createClassRoom(newRoom)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)
            assertNotNull(newClassroom)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                classroom = newClassroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByClass(newClass.id, 10, 0).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.classroom == newLecture.classroom)
            assert(foundLecture?.type == newLecture.type)
            assert(foundLecture?.weekDay == newLecture.weekDay)
            assert(foundLecture?.startTime == newLecture.startTime)
            assert(foundLecture?.endTime == newLecture.endTime)
        }
    }


    @Test
    fun `Should create a new lecture and find it by room id`() {
        kTransaction.useTransaction {
            val subject1 = Subject {
                name = "test3"
            }
            val clazz = Class {
                name = "test3"
                subject = subject1
            }

            val room = Room {
                capacity = 20
                name = "testRoom3"
            }
            val university = universityRepository.createUniversity("Test University")
            val newSubject = subjectRepository.createSubject(subject1.name, university)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name, university)
            roomRepository.createClassRoom(newRoom)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)
            assertNotNull(newClassroom)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                classroom = newClassroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByRoom(newRoom.id, 10, 0).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.classroom == newLecture.classroom)
            assert(foundLecture?.type == newLecture.type)
            assert(foundLecture?.weekDay == newLecture.weekDay)
            assert(foundLecture?.startTime == newLecture.startTime)
            assert(foundLecture?.endTime == newLecture.endTime)
        }
    }

    @Test
    fun `Should create a new lecture and find it by type`() {
        kTransaction.useTransaction {
            val subject1 = Subject {
                name = "test4"
            }
            val clazz = Class {
                name = "test4"
                subject = subject1
            }

            val room = Room {
                capacity = 20
                name = "testRoom4"
            }
            val university = universityRepository.createUniversity("Test University")
            val newSubject = subjectRepository.createSubject(subject1.name, university)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name, university)
            roomRepository.createClassRoom(newRoom)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)
            assertNotNull(newClassroom)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                classroom = newClassroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByType(ClassType.THEORETICAL).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.classroom == newLecture.classroom)
            assert(foundLecture?.type == newLecture.type)
            assert(foundLecture?.weekDay == newLecture.weekDay)
            assert(foundLecture?.startTime == newLecture.startTime)
            assert(foundLecture?.endTime == newLecture.endTime)
        }
    }

    @Test
    fun `Should delete lecture`() {
        kTransaction.useTransaction {
            val university = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Test Subject", university)
            val clazz = classRepository.addClass("Test Class", subject)
            val room = roomRepository.createRoom(20, "Test Room", university)
            roomRepository.createClassRoom(room)
            val classroom = roomRepository.getClassRoomById(room.id)
            assertNotNull(classroom)

            val lecture = lectureRepository.createLecture(
                schoolClass = clazz,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )

            assertTrue(lectureRepository.deleteLecture(lecture.id))
            assertNull(lectureRepository.getLectureById(lecture.id))
        }
    }

    @Test
    fun `Should delete lecture change`() {
        kTransaction.useTransaction {
            // Setup test data
            val university = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Test Subject", university)
            val clazz = classRepository.addClass("Test Class", subject)
            val room = roomRepository.createRoom(20, "Test Room", university)
            val newRoom = roomRepository.createRoom(20, "Test Room 2", university)
            roomRepository.createClassRoom(room)
            roomRepository.createClassRoom(newRoom)
            val classroom = roomRepository.getClassRoomById(room.id)
            val newClassroom = roomRepository.getClassRoomById(newRoom.id)

            assertNotNull(classroom)
            assertNotNull(newClassroom)

            // Create lecture and change
            val lecture = lectureRepository.createLecture(
                schoolClass = clazz,
                classroom = classroom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )

            lectureRepository.updateLecture(
                lecture, newClassroom, ClassType.PRACTICAL, WeekDay.TUESDAY, 10.hours, 12.hours, 2
            )

            // Test delete
            assertTrue(lectureRepository.deleteLectureChange(lecture.id))
            val originalLecture = lectureRepository.getLectureById(lecture.id)
            assertNotNull(originalLecture)
            assert(originalLecture.schoolClass == clazz)
            assert(originalLecture.classroom == classroom)
            assert(originalLecture.type == ClassType.THEORETICAL)
        }
    }

    @Test
    fun `Should update lecture permanently`() {
        kTransaction.useTransaction {
            // Setup test data
            val university = universityRepository.createUniversity("Test University")
            val subject = subjectRepository.createSubject("Test Subject", university)
            val clazz = classRepository.addClass("Test Class", subject)
            val room1 = roomRepository.createRoom(20, "Test Room 1", university)
            val room2 = roomRepository.createRoom(30, "Test Room 2", university)
            roomRepository.createClassRoom(room1)
            roomRepository.createClassRoom(room2)
            val classroom1 = roomRepository.getClassRoomById(room1.id)
            val classroom2 = roomRepository.getClassRoomById(room2.id)
            assertNotNull(classroom1)
            assertNotNull(classroom2)

            // Create lecture
            val lecture = lectureRepository.createLecture(
                schoolClass = clazz,
                classroom = classroom1,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.hours,
                endTime = 11.hours
            )

            // Update permanently (numberOfWeeks = 0)
            val updatedLecture = lectureRepository.updateLecture(
                lecture = lecture,
                newClassroom = classroom2,
                newType = ClassType.PRACTICAL,
                newWeekDay = WeekDay.TUESDAY,
                newStartTime = 10.hours,
                newEndTime = 12.hours,
                numberOfWeeks = 0
            )

            // Verify update
            assertEquals(classroom2, updatedLecture.classroom)
            assertEquals(ClassType.PRACTICAL, updatedLecture.type)
            assertEquals(WeekDay.TUESDAY, updatedLecture.weekDay)
            assertEquals(10.hours, updatedLecture.startTime)
            assertEquals(12.hours, updatedLecture.endTime)

            // Verify no change record was created
            val confirmUpdated = lectureRepository.getLectureById(updatedLecture.id)
            assertNotNull(confirmUpdated)
            assertEquals(confirmUpdated.id, updatedLecture.id)
            assertEquals(confirmUpdated.schoolClass, updatedLecture.schoolClass)
            assertEquals(confirmUpdated.classroom, updatedLecture.classroom)
            assertEquals(confirmUpdated.type, updatedLecture.type)

        }
    }

//    @Test
//    fun `Should update lecture temporarily`() {
//        kTransaction.useTransaction {
//            // Setup test data
//            val subject = subjectRepository.createSubject("Test Subject")
//            val clazz = classRepository.addClass("Test Class", subject)
//            val room1 = roomRepository.createRoom(20, "Test Room 1")
//            val room2 = roomRepository.createRoom(30, "Test Room 2")
//            roomRepository.createClassRoom(room1)
//            roomRepository.createClassRoom(room2)
//            val classroom1 = roomRepository.getClassRoomById(room1.id)
//            val classroom2 = roomRepository.getClassRoomById(room2.id)
//            assertNotNull(classroom1)
//            assertNotNull(classroom2)
//
//            // Create lecture
//            val lecture = lectureRepository.createLecture(
//                schoolClass = clazz,
//                classroom = classroom1,
//                type = ClassType.THEORETICAL,
//                weekDay = WeekDay.MONDAY,
//                startTime = 9.hours,
//                endTime = 11.hours
//            )
//
//            // Update temporarily (numberOfWeeks > 0)
//            val updatedLecture = lectureRepository.updateLecture(
//                lecture = lecture,
//                newClassroom = classroom2,
//                newType = ClassType.PRACTICAL,
//                newWeekDay = WeekDay.TUESDAY,
//                newStartTime = 10.hours,
//                newEndTime = 12.hours,
//                numberOfWeeks = 2
//            )
//
//            // Verify change record was created
//            val confirmUpdated = lectureRepository.getLectureById(updatedLecture.id)
//            assertNotNull(confirmUpdated)
//            assertEquals(classroom2, confirmUpdated.classroom)
//            assertEquals(ClassType.PRACTICAL, confirmUpdated.type)
//            assertEquals(WeekDay.TUESDAY, confirmUpdated.weekDay)
//            assertEquals(10.hours, confirmUpdated.startTime)
//            assertEquals(12.hours, confirmUpdated.endTime)
//        }
//    }

}