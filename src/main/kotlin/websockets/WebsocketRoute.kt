package isel.leic.group25.websockets

import io.ktor.server.routing.*


interface WebsocketRoute{
    fun Route.install()
}