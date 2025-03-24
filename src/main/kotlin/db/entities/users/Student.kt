package isel.leic.group25.db.entities.users


import org.ktorm.entity.Entity

interface Student: Entity<Student> {
    val user: User
}