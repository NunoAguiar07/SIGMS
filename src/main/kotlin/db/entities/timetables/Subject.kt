package isel.leic.group25.db.entities.timetables

import org.ktorm.entity.Entity

interface Subject: Entity<Subject> {
    val id: Int
    var name: String
}