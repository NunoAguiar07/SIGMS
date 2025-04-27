package isel.leic.group25.api.http.utils

import isel.leic.group25.db.entities.types.Role

class RoleAuthorizationConfig {
    var roles: Set<Role> = emptySet()
}