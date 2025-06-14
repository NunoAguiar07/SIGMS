package isel.leic.group25.notifications.model

import com.niamedtech.expo.exposerversdk.request.PushNotification
import kotlinx.serialization.Serializable

@Serializable
data class ExpoNotification(val title: String, val message: String, val data: NavigateToUrl): EventData {

    fun toPushNotification(to: List<String>): PushNotification{
        val pushNotification = PushNotification()
        pushNotification.to = to
        pushNotification.title = this.title
        pushNotification.body = this.message
        pushNotification.data = mapOf("url" to this.data.url)
        return pushNotification
    }

    fun toEvent(): Event {
        return Event("notification", this)
    }
}
