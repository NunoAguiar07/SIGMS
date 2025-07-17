package isel.leic.group25.notifications.websocket.route

import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import isel.leic.group25.notifications.ExpoNotifications
import isel.leic.group25.notifications.interfaces.Notifiable
import isel.leic.group25.notifications.model.ExpoNotification
import isel.leic.group25.websockets.WebsocketRoute
import isel.leic.group25.notifications.model.Event
import isel.leic.group25.notifications.model.EventData
import isel.leic.group25.notifications.model.Greet
import isel.leic.group25.notifications.websocket.model.UserConnection
import isel.leic.group25.websockets.exceptions.UnexpectedEventException
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import kotlin.reflect.full.hasAnnotation


object Notifications: WebsocketRoute {
    private val usersConnections = mutableListOf<UserConnection>()
    private val notificationDLQ = mutableMapOf<Int, MutableList<ExpoNotification>>()
    private val logger = LoggerFactory.getLogger("WebsocketNotifications")
    override fun Route.install() {
        webSocket("/notifications") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val event =  Event.eventJson.decodeFromString<Event>(frame.readText())
                        handleEvent(event.data, this)
                    }
                }
            } catch (e: Exception){
                println(e)
            }
            finally {
                usersConnections.removeIf { it.sameConnection(this) }
                this.close()
            }
        }
    }

    private fun handleEvent(event: EventData, connection: DefaultWebSocketServerSession) {
        when (event) {
            is Greet -> {
                val userConnection = UserConnection(event.userId, connection)
                usersConnections.add(userConnection)
                notificationDLQ[event.userId]?.forEach {
                    userConnection.sendNotification(it)
                }
                logger.info("Registered user:${userConnection}")
            }
            else -> throw UnexpectedEventException()
        }
    }

    fun notify(block: () -> Pair<List<Int>, Notifiable>) {
        val (users, notifiable) = block()
        logger.info("New Notification - $notifiable - to users: ${users.joinToString(",") { "$it" }}")
        val notification = notifiable.toNotification()
        ExpoNotifications.sendNotificationToUsers(users, notification)
        users.forEach { id ->
            val userConnection = usersConnections.firstOrNull { it.userId == id }
            if(userConnection == null) {
                logger.info("Saving Notification - $notifiable - for user: $id")
                notificationDLQ.computeIfAbsent(id){ mutableListOf() }.add(notification)
            } else {
                logger.info("Sending Notification - $notifiable - to user: $id")
                userConnection.sendNotification(notification)
            }
        }
    }
}