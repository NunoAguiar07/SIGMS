package isel.leic.group25.services.errors

sealed class ClassError {
    data object ClassNotFound : ClassError()
    data object ClassAlreadyExists : ClassError()
    data object InvalidClassData : ClassError()
    data object ClassChangesFailed : ClassError()
    data object MissingClassData : ClassError()
    data object InvalidRole : ClassError()
}