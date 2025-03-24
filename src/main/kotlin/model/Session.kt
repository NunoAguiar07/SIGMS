package isel.leic.group25.model

data class Session(
    val startTime: Long,
    val endTime: Long,
    val classId: Int,
    val roomId: Int,
    val subjectId: Int, // maybe delete later
    val duration: Long = endTime - startTime
)
