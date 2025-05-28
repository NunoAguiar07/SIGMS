package isel.leic.group25.api.model.response

import isel.leic.group25.websockets.hardware.enums.Capacity
import isel.leic.group25.websockets.hardware.model.Device

data class StudyRoomCapacity(val id: String, val roomName: String, val capacity: Capacity){
    companion object {
        fun Device.toStudyRoomCapacity(roomName: String): StudyRoomCapacity {
            return StudyRoomCapacity(id, roomName, capacity)
        }
    }
}
