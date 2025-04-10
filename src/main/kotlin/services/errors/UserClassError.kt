package isel.leic.group25.services.errors

sealed class UserClassError {
    data object UserNotFound : UserClassError()
    data object ClassNotFound : UserClassError()
    data object InvalidRole : UserClassError()
    data object InvalidUserClassData : UserClassError()
    data object UserClassChangesFailed : UserClassError()
    data object MissingUserClassData : UserClassError()
}