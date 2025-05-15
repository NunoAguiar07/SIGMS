package isel.leic.group25.websockets.hardware.route

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import isel.leic.group25.websockets.WebsocketRoute
import isel.leic.group25.websockets.hardware.event.*
import isel.leic.group25.websockets.hardware.exceptions.UnexpectedEventException
import isel.leic.group25.websockets.hardware.model.Device
import kotlinx.serialization.json.Json

object DeviceRoute: WebsocketRoute {
    val deviceList: MutableList<Device> = mutableListOf()
    private val deviceConnectionMap: MutableMap<String, DefaultWebSocketServerSession> = mutableMapOf()

    override fun Route.install(){
        webSocket("/ws") {
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val event = Event.eventJson.decodeFromString<Event>(frame.readText())
                        handleEvent(event.data, this)
                    }
                }
            } catch (e: UnexpectedEventException){
                println(e)
            }
            finally {
                deviceConnectionMap.filter { it.value == this }.forEach {
                    deviceConnectionMap.remove(it.key)
                    deviceList.removeAll { device -> device.id == it.key }
                }
                this.close()
            }
        }
    }

    private fun handleEvent(event: EventData, connection: DefaultWebSocketServerSession){
        when(event){
            is Hello -> {
                deviceList.add(Device(event.id))
                deviceConnectionMap[event.id] = connection
            }
            is ReceiveCapacity -> {
                val device = deviceList.first { it.id == event.id }
                deviceList.remove(device)
                deviceList.add(device.copy(capacity = event.capacity))
            }
            else -> throw UnexpectedEventException()
        }
    }

    suspend fun updateRoomCapacities(){
        deviceList.filter{ it.roomId != 0 }.forEach {
            val connection = deviceConnectionMap[it.id] ?: return@forEach
            val json = Json.encodeToString(UpdateCapacity())
            connection.send(json)
        }
    }

    suspend fun setDeviceRoom(roomId: Int): Boolean{
        val device = deviceList.firstOrNull { it.roomId == roomId } ?: return false
        val connection = deviceConnectionMap[device.id] ?: return false
        val json = Json.encodeToString(Room(roomId))
        connection.send(json)
        return true
    }
}