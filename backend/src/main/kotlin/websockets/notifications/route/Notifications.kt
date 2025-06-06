package isel.leic.group25.websockets.notifications.route

import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import isel.leic.group25.websockets.WebsocketRoute
import isel.leic.group25.websockets.exceptions.UnexpectedEventException
import isel.leic.group25.websockets.notifications.event.Acknowledge
import isel.leic.group25.websockets.notifications.event.Event
import isel.leic.group25.websockets.notifications.event.EventData
import isel.leic.group25.websockets.notifications.event.Greet
import isel.leic.group25.websockets.notifications.event.Notification
import isel.leic.group25.websockets.notifications.model.UserConnection
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.UUID
import kotlin.reflect.full.findAnnotation

object Notifications: WebsocketRoute {
    private val users = mutableListOf<UserConnection>()
    private val notificationDLQ = mutableMapOf<String, MutableList<Notification>>()

    override fun Route.install() {
        webSocket("/notifications") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val event =  Event.eventJson.decodeFromString<EventData>(frame.readText())
                        handleEvent(event, this)
                    }
                }
            } catch (e: Exception){
                println(e)
            }
            finally {
                users.firstOrNull { it.hasConnection(this) }?.removeConnection(this)
                this.close()
            }
        }
    }

    private fun handleEvent(event: EventData, connection: DefaultWebSocketServerSession) {
        when (event) {
            is Greet -> {
                val userConnection = users.firstOrNull { it.userId == event.userId } ?: UserConnection(event.userId)
                userConnection.addConnection(connection)
            }
            is Acknowledge -> {
                val userConnection = users.firstOrNull { it.userId == event.userId }
                userConnection?.notificationACK(event.id)
            }
            else -> UnexpectedEventException()
        }
    }

    fun notify(block: () -> Map<String, Any>){
        val newNotifications = block()
        newNotifications.forEach { id, data ->
            val userConnection = users.firstOrNull { it.userId == id }
            if(data::class.findAnnotation<Serializable>() == null) return@forEach
            val notification = Notification(UUID.randomUUID().toString(), id, Json.encodeToString(data))
            if(userConnection == null){
                notificationDLQ.computeIfAbsent(id){ mutableListOf() }.add(notification)
            } else {
                userConnection.sendNotification(notification)
            }
        }
    }
}