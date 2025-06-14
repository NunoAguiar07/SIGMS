package isel.leic.group25.services.email.model

import isel.leic.group25.notifications.interfaces.Notifiable
import isel.leic.group25.notifications.model.ExpoNotification
import isel.leic.group25.notifications.model.NavigateToUrl

data class UserDetails(
    val username: String,
    val email: String,
    val requestedRole: String
) : Notifiable {
    override fun toNotification(): ExpoNotification {
        return ExpoNotification("New Account Verification", "Check new accounts that need verification", NavigateToUrl("/access-roles"))
    }
}