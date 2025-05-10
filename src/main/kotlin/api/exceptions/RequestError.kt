package isel.leic.group25.api.exceptions


sealed class RequestError {
    data object InsecurePassword : RequestError()
    data object MicrosoftConnectionFailed : RequestError()

    data class Invalid(val fields: List<String>) : RequestError() {
        constructor(field: String) : this(listOf(field))
    }
    data class Missing(val fields: List<String>) : RequestError(){
        constructor(field: String) : this(listOf(field))
    }

    fun toProblem(): Problem {
        return when (this) {
            InsecurePassword -> Problem.badRequest(
                title = "Insecure password",
                detail = "The provided password does not meet security requirements."
            )
            MicrosoftConnectionFailed -> Problem.internalServerError(
                title = "External service unavailable",
                detail = "We couldn't connect to Microsoft authentication services. Please try again later."
            )
            is Missing -> Problem.badRequest(
                title = "Missing fields",
                detail = "Required fields are missing: ${fields.joinToString(", ")}"
            )

            is Invalid -> Problem.badRequest(
                title = "Invalid fields",
                detail = "Invalid values for: ${fields.joinToString(", ")}"
            )
        }
    }
}