package isel.leic.group25.db.repositories

import UniversityRepositoryInterface
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.db.repositories.issues.IssueReportRepository
import isel.leic.group25.db.repositories.issues.interfaces.IssueReportRepositoryInterface
import isel.leic.group25.db.repositories.ktorm.KtormCommand
import isel.leic.group25.db.repositories.rooms.RoomRepository
import isel.leic.group25.db.repositories.rooms.interfaces.RoomRepositoryInterface
import isel.leic.group25.db.repositories.timetables.ClassRepository
import isel.leic.group25.db.repositories.timetables.LectureRepository
import isel.leic.group25.db.repositories.timetables.SubjectRepository
import isel.leic.group25.db.repositories.timetables.UniversityRepository
import isel.leic.group25.db.repositories.timetables.interfaces.ClassRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.LectureRepositoryInterface
import isel.leic.group25.db.repositories.timetables.interfaces.SubjectRepositoryInterface
import isel.leic.group25.db.repositories.users.AdminRepository
import isel.leic.group25.db.repositories.users.RoleApprovalRepository
import isel.leic.group25.db.repositories.users.StudentRepository
import isel.leic.group25.db.repositories.users.TeacherRepository
import isel.leic.group25.db.repositories.users.TechnicalServiceRepository
import isel.leic.group25.db.repositories.users.UserRepository
import isel.leic.group25.db.repositories.users.interfaces.AdminRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.RoleApprovalRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.StudentRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TeacherRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.TechnicalServiceRepositoryInterface
import isel.leic.group25.db.repositories.users.interfaces.UserRepositoryInterface
import org.ktorm.database.Database

open class Repositories(db: Database) {
    open val universityRepository: UniversityRepositoryInterface = UniversityRepository(db)
    open val userRepository: UserRepositoryInterface = UserRepository(db)
    open val studentRepository: StudentRepositoryInterface = StudentRepository(db)
    open val teacherRepository: TeacherRepositoryInterface = TeacherRepository(db)
    open val adminRepository: AdminRepositoryInterface = AdminRepository(db)
    open val technicalServiceRepository: TechnicalServiceRepositoryInterface = TechnicalServiceRepository(db)
    open val roleApprovalRepository: RoleApprovalRepositoryInterface = RoleApprovalRepository(db)
    open val classRepository: ClassRepositoryInterface = ClassRepository(db)
    open val subjectRepository: SubjectRepositoryInterface = SubjectRepository(db)
    open val roomRepository: RoomRepositoryInterface = RoomRepository(db)
    open val lectureRepository: LectureRepositoryInterface = LectureRepository(db)
    open val issueReportRepository: IssueReportRepositoryInterface = IssueReportRepository(db)
    open val ktormCommand: Transactionable = KtormCommand(db)


    fun <R, T> from(repositorySelector: Repositories.() -> R, block: R.() -> T): T {
        val repository = this.repositorySelector()
        return repository.block()
    }
}