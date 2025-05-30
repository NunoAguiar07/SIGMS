package mocks.repositories

import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
import mocks.repositories.issues.MockIssueReportRepository
import mocks.repositories.rooms.MockRoomRepository
import mocks.repositories.timetables.MockClassRepository
import mocks.repositories.timetables.MockLectureRepository
import mocks.repositories.timetables.MockSubjectRepository
import mocks.repositories.timetables.MockUniversityRepository
import mocks.repositories.users.MockAdminRepository
import mocks.repositories.users.MockRoleApprovalRepository
import mocks.repositories.users.MockStudentRepository
import mocks.repositories.users.MockTeacherRepository
import mocks.repositories.users.MockTechnicalServiceRepository
import mocks.repositories.users.MockUserRepository
import mocks.repositories.utils.MockTransaction
import org.ktorm.database.Database


class MockRepositories(db: Database): Repositories(db) {

    override val universityRepository = MockUniversityRepository()
    override val userRepository = MockUserRepository()
    override val studentRepository = MockStudentRepository()
    override val teacherRepository = MockTeacherRepository()
    override val adminRepository = MockAdminRepository(userRepository)
    override val technicalServiceRepository = MockTechnicalServiceRepository()
    override val roleApprovalRepository = MockRoleApprovalRepository()
    override val classRepository = MockClassRepository()
    override val subjectRepository = MockSubjectRepository()
    override val roomRepository = MockRoomRepository()
    override val lectureRepository = MockLectureRepository()
    override val issueReportRepository = MockIssueReportRepository()
    override val ktormCommand: Transactionable = MockTransaction()
}