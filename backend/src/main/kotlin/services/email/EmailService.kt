package isel.leic.group25.services.email

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.services.email.model.UserDetails

interface EmailService {
    fun sendAdminApprovalRequest(adminEmails: List<String>, approvalLink: String, userDetails: UserDetails)
    fun sendUserApprovalNotification(userEmail: String, isApproved: Boolean)
    fun sendStudentVerificationEmail(userEmail: String, username: String, verificationLink: String)
    fun sendStudentsChangeInLecture(toStudents: List<String>, lecture: Lecture)
}
