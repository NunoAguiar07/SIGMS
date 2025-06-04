package isel.leic.group25.api.http.routes.authenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.api.jwt.getUniversityIdFromPrincipal
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.request.ClassRequest
import isel.leic.group25.api.model.request.SubjectRequest
import isel.leic.group25.api.model.response.ClassResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.SubjectResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.Services

/**
 * Defines all subject-related routes including:
 * - Subject management (CRUD operations)
 * - Class management within subjects
 * - User enrollment in classes
 * - Class lecture scheduling
 *
 * Routes have role-based access control with different operations available to different roles.
 *
 * @param services The class containing all the services containing business logic
 */
fun Route.subjectRoutes(
    services: Services
) {
    route("/subjects") {
        baseSubjectRoutes(services)
        specificSubjectRoutes(services)
        subjectClassesRoutes(services)

        route("/{subjectId}/classes") {
            classManagementRoutes(services)

        }
        route("/{subjectId}/classes/{classId}") {
            withRoles(setOf(Role.TEACHER, Role.STUDENT)){
                classUserRoutes(services)
            }
            classLecturesRoutes(services)
        }
    }
}

/**
 * Handles basic subject operations:
 * - List all subjects (public)
 * - Create new subject (admin only)
 */
fun Route.baseSubjectRoutes(services: Services) {
    get {
        val universityId = call.getUniversityIdFromPrincipal()
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        val searchQuery = call.queryParameters["search"]
        val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
        if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
        val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
        if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
        val result = services.from({ subjectService }) {
            if (searchQuery != null) {
                getSubjectsByNameAndUniversityId(universityId, searchQuery, limit, offset)
            } else {
                getAllSubjectsByUniversity(universityId, limit, offset)
            }
        }
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { subjects ->
                subjects.map { SubjectResponse.fromSubject(it) }
            }
        )
    }

    withRole(Role.ADMIN){
        post("/add") {
            val universityId = call.getUniversityIdFromPrincipal()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val subjectRequest = call.receive<SubjectRequest>()
            subjectRequest.validate()?.let { error ->
                return@post error.toProblem().respond(call)
            }
            val result = services.from({subjectService}){
                createSubject(
                    name = subjectRequest.name,
                    universityId = universityId,
                )
            }
            call.respondEither(
                either = result,
                transformError = { it.toProblem() },
                transformSuccess = { subject ->
                    SubjectResponse.fromSubject(subject)
                },
                successStatus = HttpStatusCode.Created
            )
        }
    }

}

/**
 * Handles operations for specific subjects:
 * - Get subject details (public)
 * - Delete subject (admin only)
 */
fun Route.specificSubjectRoutes(services: Services) {
    route("/{subjectId}") {
        get {
            val subjectId = call.parameters["subjectId"]
            if(subjectId.isNullOrBlank()) return@get RequestError.Missing("subjectId").toProblem().respond(call)
            if(subjectId.toIntOrNull() == null) return@get RequestError.Invalid("subjectId").toProblem().respond(call)
            val result = services.from({subjectService}){
                getSubjectById(subjectId.toInt())
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { subject ->
                    SubjectResponse.fromSubject(subject)
                }
            )
        }

        withRole(Role.ADMIN){
            delete("/delete") {
                val subjectId = call.parameters["subjectId"]
                if(subjectId.isNullOrBlank()) return@delete RequestError.Missing("subjectId").toProblem().respond(call)
                if(subjectId.toIntOrNull() == null) return@delete RequestError.Invalid("subjectId").toProblem().respond(call)
                val result = services.from({subjectService}){
                    deleteSubject(subjectId.toInt())
                }
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = {
                        HttpStatusCode.NoContent
                    }
                )
            }
        }
    }
}

/**
 * Handles class operations within subjects:
 * - List all classes for a subject (public)
 * - Create new class (admin only)
 */
fun Route.subjectClassesRoutes(services: Services) {
    route("/{subjectId}/classes") {
        get {
            val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
            if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
            val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            val subjectId = call.parameters["subjectId"]
            if(subjectId.isNullOrBlank()) return@get RequestError.Missing("subjectId").toProblem().respond(call)
            if(subjectId.toIntOrNull() == null) return@get RequestError.Invalid("subjectId").toProblem().respond(call)
            val result = services.from({classService}){
                getAllClassesFromSubject(subjectId.toInt(), limit, offset)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { classes ->
                    classes.map { ClassResponse.fromClass(it) }
                }
            )
        }

        withRole(Role.ADMIN){
            post("/add") {
                val subjectId = call.parameters["subjectId"]
                if(subjectId.isNullOrBlank()) return@post RequestError.Missing("subjectId").toProblem().respond(call)
                if(subjectId.toIntOrNull() == null) return@post RequestError.Invalid("subjectId").toProblem().respond(call)
                val classRequest = call.receive<ClassRequest>()
                val result = services.from({classService}){
                    createClass(
                        name = classRequest.name,
                        subjectId = subjectId.toInt()
                    )
                }
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { schoolClass ->
                        ClassResponse.fromClass(schoolClass)
                    },
                    successStatus = HttpStatusCode.Created
                )
            }
        }
    }
}

/**
 * Handles operations for specific classes:
 * - Get class details (public)
 */
fun Route.classManagementRoutes(services: Services) {
    route("/{classId}") {
        get {
            val classId = call.parameters["classId"]
            if(classId.isNullOrBlank()) return@get RequestError.Missing("classId").toProblem().respond(call)
            if(classId.toIntOrNull() == null) return@get RequestError.Invalid("classId").toProblem().respond(call)
            val result = services.from({classService}){
                getClassById(classId.toInt())
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { schoolClass ->
                    ClassResponse.fromClass(schoolClass)
                }
            )
        }
    }
}

/**
 * Handles user enrollment in classes:
 * - Add user to class (teacher/student only)
 * - Remove user from class (teacher/student only)
 * - get all user classes (teacher/student only)
 */
fun Route.classUserRoutes(services: Services) {
    route("/users") {
        post("/add") {
            val role = call.getUserRoleFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val classId = call.parameters["classId"]
            if(classId.isNullOrBlank()) return@post RequestError.Missing("classId").toProblem().respond(call)
            if(classId.toIntOrNull() == null) return@post RequestError.Invalid("classId").toProblem().respond(call)
            val result = services.from({userClassService}){
                addUserToClass(userId, classId.toInt(), Role.valueOf(role.uppercase()))
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = {
                    HttpStatusCode.NoContent
                }
            )
        }

        delete("/remove") {
            val role = call.getUserRoleFromPrincipal() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val userId = call.getUserIdFromPrincipal() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
            val classId = call.parameters["classId"]
            if(classId.isNullOrBlank()) return@delete RequestError.Missing("classId").toProblem().respond(call)
            if(classId.toIntOrNull() == null) return@delete RequestError.Invalid("classId").toProblem().respond(call)
            val result = services.from({userClassService}){
                removeUserFromClass(userId, classId.toInt(), Role.valueOf(role.uppercase()))
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = {
                    HttpStatusCode.NoContent
                }
            )
        }


    }
}

/**
 * Handles lecture operations for classes:
 * - List all lectures for a class (public)
 */
fun Route.classLecturesRoutes(services: Services) {
    route("/lectures") {
        get {
            val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
            if(limit < 1 || limit > 100) return@get RequestError.Invalid("limit").toProblem().respond(call)
            val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
            if(offset < 0) return@get RequestError.Invalid("offset").toProblem().respond(call)
            val id = call.parameters["classId"]
            if(id.isNullOrBlank()) return@get RequestError.Missing("classId").toProblem().respond(call)
            if(id.toIntOrNull() == null) return@get RequestError.Invalid("classId").toProblem().respond(call)
            val result = services.from({lectureService}){
                getLecturesByClass(id.toInt(), limit, offset)
            }
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { lectures ->
                    lectures.map { LectureResponse.from(it) }
                }
            )
        }
    }
}

