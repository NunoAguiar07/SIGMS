package isel.leic.group25.db.repositories.users.interfaces

import isel.leic.group25.db.entities.users.Admin
import isel.leic.group25.db.entities.users.User

interface AdminRepositoryInterface {
     fun findAdminById(id: Int): Admin?

     fun findAdminByEmail(email: String): Admin?

     fun isAdmin(user: User): Boolean

     fun User.toAdmin(): Admin
}