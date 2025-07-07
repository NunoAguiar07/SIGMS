package isel.leic.group25.db.entities.rooms

import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.tables.Tables.Companion.officeRooms
import kotlinx.serialization.Serializable
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.Entity
import org.ktorm.entity.firstOrNull

@Serializable
sealed interface Room: Entity<Room> {
    companion object: Entity.Factory<Room>()
    val id : Int
    var name : String
    var capacity : Int
    var university: University

    fun toOfficeRoom(database: Database) : OfficeRoom =
        database.officeRooms
            .firstOrNull { it.id eq id }
            ?: throw NoSuchElementException("No office room found with id $id")

}