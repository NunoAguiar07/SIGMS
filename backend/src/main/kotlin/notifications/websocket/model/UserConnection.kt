package isel.leic.group25.notifications.websocket.model

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.send
import isel.leic.group25.notifications.model.ExpoNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.ktorm.logging.Logger
import org.slf4j.LoggerFactory

data class UserConnection(val userId: Int, private val connection: DefaultWebSocketServerSession){
    private val logger = LoggerFactory.getLogger("ExpoNotifications")
    private val notificationsFlow = MutableSharedFlow<ExpoNotification>(extraBufferCapacity = 1)
    init {
        connection.launch {
            notificationsFlow.collect {
                val event = it.toEvent()
                val json = Json.encodeToString(event)
                try{
                    logger.info("Sending event: ${event}")
                    connection.send(json)
                } catch (e: Exception){
                    logger.error("Could not send event: ${e.message}")
                    println(e)
                }
            }
        }
    }

    fun sameConnection(connection: DefaultWebSocketServerSession) = connection == this.connection

    fun sendNotification(notification: ExpoNotification){
        connection.launch {
            notificationsFlow.emit(notification)
        }
    }
}