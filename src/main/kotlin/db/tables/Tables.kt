package isel.leic.group25.db.tables

import isel.leic.group25.db.tables.issues.IssueReports
import isel.leic.group25.db.tables.rooms.Classrooms
import isel.leic.group25.db.tables.rooms.OfficeRooms
import isel.leic.group25.db.tables.rooms.Rooms
import isel.leic.group25.db.tables.rooms.StudyRooms
import isel.leic.group25.db.tables.timetables.Classes
import isel.leic.group25.db.tables.timetables.LectureChanges
import isel.leic.group25.db.tables.timetables.Lectures
import isel.leic.group25.db.tables.timetables.Subjects
import isel.leic.group25.db.tables.users.*
import isel.leic.group25.db.tables.users.Attends
import isel.leic.group25.db.tables.users.Teaches
import org.ktorm.database.Database
import org.ktorm.entity.sequenceOf

class Tables {
    companion object {
        val Database.issueReports get() = this.sequenceOf(IssueReports)
        val Database.classrooms get() = this.sequenceOf(Classrooms)
        val Database.officeRooms get() = this.sequenceOf(OfficeRooms)
        val Database.rooms get() = this.sequenceOf(Rooms)
        val Database.studyRooms get() = this.sequenceOf(StudyRooms)
        val Database.classes get() = this.sequenceOf(Classes)
        val Database.lectures get() = this.sequenceOf(Lectures)
        val Database.lectureChanges get() = this.sequenceOf(LectureChanges)
        val Database.subjects get() = this.sequenceOf(Subjects)
        val Database.admins get() = this.sequenceOf(Admins)
        val Database.students get() = this.sequenceOf(Students)
        val Database.teachers get() = this.sequenceOf(Teachers)
        val Database.technicalServices get() = this.sequenceOf(TechnicalServices)
        val Database.users get() = this.sequenceOf(Users)
        val Database.pendingRoleApprovals get() = this.sequenceOf(RoleApprovals)
        val Database.teachersClasses get() = this.sequenceOf(Teaches)
        val Database.studentsClasses get() = this.sequenceOf(Attends)
    }
}