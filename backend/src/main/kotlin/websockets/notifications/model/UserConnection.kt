package isel.leic.group25.websockets.notifications.model

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.send
import isel.leic.group25.websockets.notifications.event.Notification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class UserConnection(val userId: String, private val connections: MutableList<DefaultWebSocketServerSession> = mutableListOf()){
    private val notificationsFlow = MutableSharedFlow<Notification>(extraBufferCapacity = 1)
    private val notificationsRecord = mutableListOf<Notification>()
    val notifyJob = connections.first().launch {
        notificationsFlow.collect {
            val event = it.toEvent()
            val json = Json.encodeToString(event)
            try{
                connections.forEach { connection ->
                    connection.send(json)
                }
            } catch (e: Exception){
                println(e)
            }
        }
    }

    fun addConnection(connection: DefaultWebSocketServerSession){
        connections.add(connection)
        connection.launch {
            notificationsRecord.forEach {
                notificationsFlow.emit(it)
            }
        }
    }

    fun hasConnection(connection: DefaultWebSocketServerSession) = connections.contains(connection)

    fun removeConnection(connection: DefaultWebSocketServerSession){
        connections.remove(connection)
    }

    fun sendNotification(notification: Notification){
        connections.firstOrNull()?.launch {
            notificationsFlow.emit(notification)
        }
        notificationsRecord.add(notification)
    }

    fun notificationACK(notificationId: String){
        notificationsRecord.removeIf { it.id == notificationId }
    }
}