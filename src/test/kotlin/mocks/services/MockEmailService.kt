package mocks.services

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.email.model.UserDetails

sealed class SentEmail {
    data class AdminApproval(val email: String, val link: String) : SentEmail()
    data class UserNotification(val email: String, val isApproved: Boolean) : SentEmail()
    data class Verification(val email: String, val link: String) : SentEmail()
    data class LectureChange(val email: String, val lecture: Lecture) : SentEmail()
}

class MockEmailService : EmailService {
    val sentEmails = mutableListOf<SentEmail>()

    override fun sendAdminApprovalRequest(adminEmails: List<String>, approvalLink: String, userDetails: UserDetails) {
        sentEmails.add(SentEmail.AdminApproval(adminEmails.first(), approvalLink))
    }

    override fun sendUserApprovalNotification(userEmail: String, isApproved: Boolean) {
        sentEmails.add(SentEmail.UserNotification(userEmail, isApproved))
    }

    override fun sendStudentVerificationEmail(userEmail: String, username: String, verificationLink: String) {
        sentEmails.add(SentEmail.Verification(userEmail, verificationLink))
    }

    override fun sendStudentsChangeInLecture(toStudents: List<String>, lecture: Lecture) {
        toStudents.forEach {
            sentEmails.add(SentEmail.LectureChange(it, lecture))
        }
    }
}