package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.jwt.getUserIdFromPrincipal
import isel.leic.group25.api.jwt.getUserRoleFromPrincipal
import isel.leic.group25.api.model.request.ClassRequest
import isel.leic.group25.api.model.request.SubjectRequest
import isel.leic.group25.api.model.response.ClassResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.SubjectResponse
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.SubjectService
import isel.leic.group25.services.UserClassService

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
            classUserRoutes(usersClassService)
            classLecturesRoutes(lectureService)
        }
    }
}

fun Route.baseSubjectRoutes(subjectService: SubjectService) {
    get {
        val limit = call.queryParameters["limit"]
        val offset = call.queryParameters["offset"]
        val result = subjectService.getAllSubjects(limit, offset)
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { subjects ->
                subjects.map { SubjectResponse.fromSubject(it) }
            }
        )
    }

    post {
        val subjectRequest = call.receive<SubjectRequest>()
        val result = subjectService.createSubject(
            name = subjectRequest.name
        )
        call.respondEither(
            either = result,
            transformError = { error -> error.toProblem() },
            transformSuccess = { subject ->
                SubjectResponse.fromSubject(subject)
            },
            successStatus = HttpStatusCode.Created
        )
    }
}

fun Route.specificSubjectRoutes(subjectService: SubjectService) {
    route("/{subjectId}") {
        get {
            val id = call.parameters["subjectId"]
            val result = subjectService.getSubjectById(id)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { subject ->
                    SubjectResponse.fromSubject(subject)
                }
            )
        }

        delete {
            val id = call.parameters["subjectId"]
            val result = subjectService.deleteSubject(id)
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

fun Route.subjectClassesRoutes(classService: ClassService) {
    route("/{subjectId}/classes") {
        get {
            val limit = call.queryParameters["limit"]
            val offset = call.queryParameters["offset"]
            val id = call.parameters["subjectId"]
            val result = classService.getAllClassesFromSubject(id, limit, offset)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = { classes ->
                    classes.map { ClassResponse.fromClass(it) }
                }
            )
        }

        post {
            val id = call.parameters["subjectId"]
            val classRequest = call.receive<ClassRequest>()
            val result = classService.createClass(
                name = classRequest.name,
                subjectId = id
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

fun Route.classManagementRoutes(classService: ClassService) {
    route("/{classId}") {
        get {
            val id = call.parameters["classId"]
            val result = classService.getClassById(id)
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

fun Route.classUserRoutes(usersClassService: UserClassService) {
    route("/users") {
        post {
            val role = call.getUserRoleFromPrincipal()
            val userId = call.getUserIdFromPrincipal()
            val id = call.parameters["classId"]
            val result = usersClassService.addUserToClass(userId, id, role)
            call.respondEither(
                either = result,
                transformError = { error -> error.toProblem() },
                transformSuccess = {
                    HttpStatusCode.NoContent
                }
            )
        }

        delete {
            val role = call.getUserRoleFromPrincipal()
            val userId = call.getUserIdFromPrincipal()
            val classId = call.parameters["classId"]
            val result = usersClassService.removeUserFromClass(userId, classId, role)
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

fun Route.classLecturesRoutes(lectureService: LectureService) {
    route("/lectures") {
        get {
            val limit = call.queryParameters["limit"]
            val offset = call.queryParameters["offset"]
            val id = call.parameters["classId"]
            val result = lectureService.getLecturesByClass(id, limit, offset)
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

