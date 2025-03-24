package isel.leic.group25.db.entities.rooms

import org.ktorm.entity.Entity

interface StudyRoom: Entity<StudyRoom> {
    val room: Room
}