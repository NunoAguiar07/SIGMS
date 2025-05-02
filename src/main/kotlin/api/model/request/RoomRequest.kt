package isel.leic.group25.api.model.request

import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.db.entities.types.RoomType
import kotlinx.serialization.Serializable

@Serializable
data class RoomRequest(
    val name: String,
    val capacity: Int,
    val type: String,
) {
    fun validate(): RequestError? {
        if(name.isBlank()){
            return RequestError.Missing("name")
        }
        if(capacity <= 0){
            return RequestError.Invalid("capacity")
        }
        if(RoomType.fromValue(type) == null){
            return RequestError.Invalid("type")
        }
        return null
    }
}