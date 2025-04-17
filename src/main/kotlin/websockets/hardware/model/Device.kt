package isel.leic.group25.websockets.hardware.model

import isel.leic.group25.websockets.hardware.enums.Capacity


data class Device(val id: String = "", val roomId: Int = 0, val capacity: Capacity = Capacity.EMPTY)