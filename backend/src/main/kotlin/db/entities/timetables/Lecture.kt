package isel.leic.group25.db.entities.timetables

import isel.leic.group25.db.entities.rooms.Classroom
import isel.leic.group25.db.entities.types.ClassType
import isel.leic.group25.db.entities.types.WeekDay
import isel.leic.group25.notifications.interfaces.Notifiable
import isel.leic.group25.notifications.model.ExpoNotification
import isel.leic.group25.notifications.model.NavigateToUrl
import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity
import kotlin.time.Duration

@Serializable
sealed interface Lecture: Entity<Lecture>, Notifiable {
    companion object: Entity.Factory<Lecture>()
    val id: Int
    var schoolClass: Class
    var classroom: Classroom
    var type: ClassType
    var weekDay: WeekDay
    var startTime: Duration
    var endTime: Duration
    val duration: Duration
        get() = endTime - startTime

    override fun toNotification(): ExpoNotification {
        return  ExpoNotification("Schedule Change", "Check your schedule for affect lecture", NavigateToUrl("/calendar"))
    }
}