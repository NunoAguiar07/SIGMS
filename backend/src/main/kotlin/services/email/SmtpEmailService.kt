package isel.leic.group25.services.email

import isel.leic.group25.db.entities.timetables.Lecture
import isel.leic.group25.services.email.model.EmailConfig
import isel.leic.group25.services.email.model.UserDetails
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailException
import org.apache.commons.mail.SimpleEmail

class SmtpEmailService(private val config: EmailConfig) : EmailService {

    override fun sendAdminApprovalRequest(adminEmails: List<String>, approvalLink: String, userDetails: UserDetails) {
        try {
            val email = SimpleEmail().apply {
                hostName = config.host
                setSmtpPort(config.port)
                setAuthenticator(DefaultAuthenticator(config.username, config.password))
                isSSLOnConnect = config.useSsl
                setFrom(config.from)
                subject = "New Role Approval Request"
                setMsg("""
                    A new user has requested ${userDetails.requestedRole} role.
                    
                    User Details:
                    - Name: ${userDetails.username}
                    - Email: ${userDetails.email}
                    
                    Please review and take action:
                    $approvalLink
                """.trimIndent())
                adminEmails.forEach { addTo(it) }
            }
            email.send()
        } catch (e: Exception) {
            throw EmailException("Failed to send approval request")
        }
    }

    override fun sendUserApprovalNotification(userEmail: String, isApproved: Boolean) {
        try {
            val email = SimpleEmail().apply {
                hostName = config.host
                setSmtpPort(config.port)
                setAuthenticator(DefaultAuthenticator(config.username, config.password))
                isSSLOnConnect = config.useSsl
                setFrom(config.from)
                subject = if (isApproved) "Your Account Has Been Approved" else "Your Account Request Was Declined"
                setMsg(if (isApproved) {
                    """
                    Your account request has been approved by an administrator.
                    
                    You can now log in and access the system.
                    """.trimIndent()
                } else {
                    """
                    Your account request has been reviewed and declined by an administrator.
                    
                    Please contact support if you believe this was a mistake.
                    """.trimIndent()
                })
                addTo(userEmail)
            }
            email.send()
        } catch (e: Exception) {
            println(e.message)
            println(e.cause)
            throw e
        }
    }

    override fun sendStudentVerificationEmail(userEmail: String, username: String, verificationLink: String) {
        try {
            val email = SimpleEmail().apply {
                hostName = config.host
                setSmtpPort(config.port)
                setAuthenticator(DefaultAuthenticator(config.username, config.password))
                isSSLOnConnect = config.useSsl
                setFrom(config.from)
                subject = "Verify Your Student Account"
                setMsg("""
                Hello $username,
                
                Thank you for registering as a student! 
                Please click the link below to verify your account:
                
                $verificationLink
                
                This link will expire in 7 days.
            """.trimIndent())
                addTo(userEmail)
            }
            email.send()
        } catch (e: Exception) {
            throw e
        }
    }

    override fun sendStudentsChangeInLecture(toStudents: List<String>, lecture: Lecture) {
        try {
            val email = SimpleEmail().apply {
                hostName = config.host
                setSmtpPort(config.port)
                setAuthenticator(DefaultAuthenticator(config.username, config.password))
                isSSLOnConnect = config.useSsl
                setFrom(config.from)
                subject = "Change in schedule for your ${lecture.schoolClass.subject.name} lecture"
                setMsg("""
                    There has been a change for your lecture of ${lecture.schoolClass.subject.name} for class ${lecture.schoolClass.name}. 
                    Here is the updated lecture:
                    
                    Lecture:
                    - Subject: ${lecture.schoolClass.subject.name}
                    - Class: ${lecture.schoolClass.name}
                    - Room: ${lecture.classroom.room.name}
                    - On: ${lecture.weekDay.name}
                    - Starts at: ${lecture.startTime}
                    - Ends at: ${lecture.endTime}
                    - Type: ${lecture.type.name}
                    
                    Hope you have an amazing class!!
                """.trimIndent())
                toStudents.forEach { addTo(it) }
            }
            email.send()
        } catch (e: Exception) {
            throw e
        }
    }
}