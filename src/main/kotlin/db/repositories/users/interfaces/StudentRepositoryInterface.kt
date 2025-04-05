package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.users.Student
import isel.leic.group25.db.entities.users.User

interface StudentRepositoryInterface {
     fun findStudentById(id: Int): Student?

     fun findStudentByEmail(email: String): Student?

     fun isStudent(user: User): Boolean

     fun User.toStudent(): Student
}