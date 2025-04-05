package isel.leic.group25.db.exceptions.users

class UserNotInRole(override val message: String = "User can only be converted to correct role") : Exception(message)