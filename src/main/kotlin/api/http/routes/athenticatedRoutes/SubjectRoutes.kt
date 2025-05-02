package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.RequestError
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.http.utils.withRole
import isel.leic.group25.api.http.utils.withRoles
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.request.ClassRequest
import isel.leic.group25.api.model.request.SubjectRequest
import isel.leic.group25.api.model.response.ClassResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.SubjectResponse
import isel.leic.group25.db.entities.types.Role
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.UserClassService

/**
 * Defines all subject-related routes including:
 * - Subject management (CRUD operations)
 * - Class management within subjects
 * - User enrollment in classes
 * - Class lecture scheduling
 *
 * Routes have role-based access control with different operations available to different roles.
 *
 * @param subjectService Service handling subject operations
 * @param classService Service handling class operations
 * @param usersClassService Service handling user-class relationships
 * @param lectureService Service handling lecture scheduling
 */
fun Route.subjectRoutes(
    subjectService: SubjectService,
    classService: ClassService,
    usersClassService: UserClassService,
    lectureService: LectureService
) {
    route("/subjects") {
        baseSubjectRoutes(subjectService)
        specificSubjectRoutes(subjectService)
        subjectClassesRoutes(classService)

        route("/{subjectId}/classes/{classId}") {
            classManagementRoutes(classService)
            withRoles(setOf(Role.TEACHER, Role.STUDENT)){
                classUserRoutes(usersClassService)
            }
            classLecturesRoutes(lectureService)
        }
    }
}

/**
 * Handles basic subject operations:
 * - List all subjects (public)
 * - Create new subject (admin only)
 */
fun Route.baseSubjectRoutes(subjectService: SubjectService) {
    get {
        val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
        if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
        val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
        if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
        val result = subjectService.getAllSubjects(limit, offset)
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
            val subjectRequest = call.receive<SubjectRequest>()
            subjectRequest.validate()?.let { error ->
                return@post call.respond(error.toProblem())
            }
            val result = subjectService.createSubject(
                name = subjectRequest.name
            )
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
fun Route.specificSubjectRoutes(subjectService: SubjectService) {
    route("/{subjectId}") {
        get {
            val subjectId = call.parameters["subjectId"]
            if(subjectId.isNullOrBlank()) return@get call.respond(RequestError.Missing("subjectId").toProblem())
            if(subjectId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("subjectId").toProblem())
            val result = subjectService.getSubjectById(subjectId.toInt())
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
                if(subjectId.isNullOrBlank()) return@delete call.respond(RequestError.Missing("subjectId").toProblem())
                if(subjectId.toIntOrNull() == null) return@delete call.respond(RequestError.Invalid("subjectId").toProblem())
                val result = subjectService.deleteSubject(subjectId.toInt())
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
fun Route.subjectClassesRoutes(classService: ClassService) {
    route("/{subjectId}/classes") {
        get {
            val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
            if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
            val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
            if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
            val subjectId = call.parameters["subjectId"]
            if(subjectId.isNullOrBlank()) return@get call.respond(RequestError.Missing("subjectId").toProblem())
            if(subjectId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("subjectId").toProblem())
            val result = classService.getAllClassesFromSubject(subjectId.toInt(), limit, offset)
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
                if(subjectId.isNullOrBlank()) return@post call.respond(RequestError.Missing("subjectId").toProblem())
                if(subjectId.toIntOrNull() == null) return@post call.respond(RequestError.Invalid("subjectId").toProblem())
                val classRequest = call.receive<ClassRequest>()
                val result = classService.createClass(
                    name = classRequest.name,
                    subjectId = subjectId.toInt()
                )
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
fun Route.classManagementRoutes(classService: ClassService) {
    route("/{classId}") {
        get {
            val classId = call.parameters["classId"]
            if(classId.isNullOrBlank()) return@get call.respond(RequestError.Missing("classId").toProblem())
            if(classId.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("classId").toProblem())
            val result = classService.getClassById(classId.toInt())
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
 */
fun Route.classUserRoutes(usersClassService: UserClassService) {
    route("/users") {
        post("/add") {
            val role = call.getUserRoleFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val userId = call.getUserIdFromPrincipal() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val classId = call.parameters["classId"]
            if(classId.isNullOrBlank()) return@post call.respond(RequestError.Missing("classId").toProblem())
            if(classId.toIntOrNull() == null) return@post call.respond(RequestError.Invalid("classId").toProblem())
            val result = usersClassService.addUserToClass(userId, classId.toInt(), Role.valueOf(role.uppercase()))
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
            if(classId.isNullOrBlank()) return@delete call.respond(RequestError.Missing("classId").toProblem())
            if(classId.toIntOrNull() == null) return@delete call.respond(RequestError.Invalid("classId").toProblem())
            val result = usersClassService.removeUserFromClass(userId, classId.toInt(), Role.valueOf(role.uppercase()))
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
fun Route.classLecturesRoutes(lectureService: LectureService) {
    route("/lectures") {
        get {
            val limit = call.queryParameters["limit"] ?.toIntOrNull()?: 10
            if(limit < 1 || limit > 100) return@get call.respond(RequestError.Invalid("limit").toProblem())
            val offset = call.queryParameters["offset"] ?.toIntOrNull() ?: 0
            if(offset < 0) return@get call.respond(RequestError.Invalid("offset").toProblem())
            val id = call.parameters["classId"]
            if(id.isNullOrBlank()) return@get call.respond(RequestError.Missing("classId").toProblem())
            if(id.toIntOrNull() == null) return@get call.respond(RequestError.Invalid("classId").toProblem())
            val result = lectureService.getLecturesByClass(id.toInt(), limit, offset)
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

