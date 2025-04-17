package isel.leic.group25.websockets.hardware.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Serializable
sealed interface Event {
    @SerialName("t") val eventCode: String
    val timestamp: Duration get() = System.currentTimeMillis().toDuration(DurationUnit.MILLISECONDS)
}