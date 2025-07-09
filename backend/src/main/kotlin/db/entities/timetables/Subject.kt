package isel.leic.group25.db.entities.timetables

import kotlinx.serialization.Serializable
import org.ktorm.entity.Entity

@Serializable
sealed interface Subject: Entity<Subject> {
    companion object: Entity.Factory<Subject>()
    var id: Int
    var name: String
    var university: University
}