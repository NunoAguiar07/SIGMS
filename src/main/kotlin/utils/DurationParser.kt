package isel.leic.group25.utils

import kotlin.time.Duration
import kotlin.time.toDuration

fun String.hoursAndMinutesToDuration(): Duration {
    val (hours, minutes) = this.split(":").map { it.toLong() }
    return (hours * 60 + minutes).toDuration(kotlin.time.DurationUnit.MINUTES)
}

fun Duration.toHoursAndMinutes(): String{
    val hours = this.inWholeMinutes / 60
    val minutes = this.inWholeMinutes % 60
    return "$hours:${if(minutes < 10) "0$minutes" else minutes}"
}