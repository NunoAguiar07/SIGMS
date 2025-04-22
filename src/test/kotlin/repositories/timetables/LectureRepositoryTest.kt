package repositories.timetables

import isel.leic.group25.db.entities.timetables.Class
import isel.leic.group25.db.repositories.ktorm.KTransaction
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.db.entities.timetables.Subject
import isel.leic.group25.db.entities.rooms.Room
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import repositories.DatabaseTestSetup
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.minutes

class LectureRepositoryTest {
    private val kTransaction = KTransaction(DatabaseTestSetup.database)
    private val lectureRepository = LectureRepository(DatabaseTestSetup.database)
    private val classRepository = ClassRepository(DatabaseTestSetup.database)
    private val roomRepository = RoomRepository(DatabaseTestSetup.database)
    private val subjectRepository = SubjectRepository(DatabaseTestSetup.database)

    @AfterTest
    fun clearDatabase() {
        DatabaseTestSetup.clearDB()
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
            val newSubject = subjectRepository.createSubject(subject1.name)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                room = newRoom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getAllLectures().firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.room == newLecture.room)
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
            val newSubject = subjectRepository.createSubject(subject1.name)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                room = newRoom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByClass(newClass.id, 10, 0).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.room == newLecture.room)
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
            val newSubject = subjectRepository.createSubject(subject1.name)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                room = newRoom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByRoom(newRoom.id, 10, 0).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.room == newLecture.room)
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
            val newSubject = subjectRepository.createSubject(subject1.name)
            val newClass = classRepository.addClass(clazz.name, newSubject)
            val newRoom = roomRepository.createRoom(room.capacity,room.name)
            val newLecture = lectureRepository.createLecture(
                schoolClass = newClass,
                room = newRoom,
                type = ClassType.THEORETICAL,
                weekDay = WeekDay.MONDAY,
                startTime = 9.minutes,
                endTime = 11.minutes
            )
            val foundLecture = lectureRepository.getLecturesByType(ClassType.THEORETICAL).firstOrNull { it == newLecture }
            assert(foundLecture != null)
            assert(foundLecture?.schoolClass == newLecture.schoolClass)
            assert(foundLecture?.room == newLecture.room)
            assert(foundLecture?.type == newLecture.type)
            assert(foundLecture?.weekDay == newLecture.weekDay)
            assert(foundLecture?.startTime == newLecture.startTime)
            assert(foundLecture?.endTime == newLecture.endTime)
        }
    }




}