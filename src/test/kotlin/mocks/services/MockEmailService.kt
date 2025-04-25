package mocks.services

import isel.leic.group25.services.email.EmailService
import isel.leic.group25.services.email.model.UserDetails

class MockEmailService : EmailService {
    val sentEmails = mutableListOf<Pair<String, String>>() // <email, content>

    override fun sendAdminApprovalRequest(adminEmails: List<String>, approvalLink: String, userDetails: UserDetails) {
        sentEmails.add(adminEmails.first() to approvalLink)
    }

    override fun sendUserApprovalNotification(userEmail: String, isApproved: Boolean) {
        sentEmails.add(userEmail to isApproved.toString())
    }

    override fun sendStudentVerificationEmail(userEmail: String, username: String, verificationLink: String) {
        sentEmails.add(userEmail to verificationLink)
    }
}