package isel.leic.group25.model

data class ClassRoom(
    override val id: Int,
    override val capacity: Int,
) : Room(id, capacity)