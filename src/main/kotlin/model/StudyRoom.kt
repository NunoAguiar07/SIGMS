package isel.leic.group25.model

data class StudyRoom(
    override val id: Int,
    override val capacity: Int,
) : Room(id, capacity)