package isel.leic.group25.notifications

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient
import com.niamedtech.expo.exposerversdk.response.Status
import com.niamedtech.expo.exposerversdk.response.TicketResponse
import isel.leic.group25.notifications.model.ExpoNotification
import isel.leic.group25.notifications.model.UserPushToken
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.slf4j.LoggerFactory

object ExpoNotifications {
    private val logger = LoggerFactory.getLogger("ExpoNotifications")
    private val userPushTokens = mutableListOf<UserPushToken>()
    private val httpClient = HttpClients.createDefault();
    private val expoPushNotificationClient = ExpoPushNotificationClient
        .builder()
        .setHttpClient(httpClient)
        .build()

    fun saveToken(userId: Int, expoPushToken: String){
        logger.info("Registered User :${userId}")
        userPushTokens.add(UserPushToken(userId, expoPushToken))
    }

    fun sendNotificationToUsers(userIds: List<Int>, expoNotification: ExpoNotification) {
        val tokens = userPushTokens.filter { userIds.contains(it.userId) }.map { it.expoPushToken }
        val pushNotification = expoNotification.toPushNotification(tokens)
        val response: List<TicketResponse.Ticket> = expoPushNotificationClient.sendPushNotifications(listOf(pushNotification))
        response.forEach { ticket ->
            if(ticket.status == Status.ERROR){
                val token = ticket.details.expoPushToken
                userPushTokens.removeIf { it.expoPushToken == token }
            }
        }
    }
}