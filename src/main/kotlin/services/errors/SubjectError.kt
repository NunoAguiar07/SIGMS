package isel.leic.group25.services.errors

sealed class SubjectError {
    data object SubjectNotFound : SubjectError()
    data object FailedToAddToDatabase : SubjectError()
    data object SubjectAlreadyExists : SubjectError()
    data object InvalidSubjectData : SubjectError()
    data object InvalidSubjectId : SubjectError()
    data object SubjectChangesFailed : SubjectError()
    data object MissingSubjectData : SubjectError()
    data object InvalidRole : SubjectError()

}