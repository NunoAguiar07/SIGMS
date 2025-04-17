package isel.leic.group25.api.http.routes.athenticatedRoutes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import isel.leic.group25.api.exceptions.respondEither
import isel.leic.group25.api.model.request.ClassRequest
import isel.leic.group25.api.model.request.SubjectRequest
import isel.leic.group25.api.model.response.ClassResponse
import isel.leic.group25.api.model.response.LectureResponse
import isel.leic.group25.api.model.response.SubjectResponse
import isel.leic.group25.services.ClassService
import isel.leic.group25.services.LectureService
import isel.leic.group25.services.SubjectService

fun Route.subjectRoutes(
    subjectService: SubjectService,
    classService: ClassService,
    lectureService: LectureService
) {
    route("/subjects") {
        get {
            val limit = call.parameters["limit"]
            val offset = call.parameters["offset"]
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
        route("/{subjectId}") {
            get {
                val id = call.parameters["subjectId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val result = subjectService.getSubjectById(id)
                call.respondEither(
                    either = result,
                    transformError = { error -> error.toProblem() },
                    transformSuccess = { subject ->
                        SubjectResponse.fromSubject(subject)
                    }
                )
            }
            route("/classes") {
                get {
                    val limit = call.parameters["limit"]
                    val offset = call.parameters["offset"]
                    val id = call.parameters["subjectId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
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
                    val id = call.parameters["subjectId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
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
                route("/{classId}") {
                    get {
                        val id = call.parameters["classId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                        val result = classService.getClassById(id)
                        call.respondEither(
                            either = result,
                            transformError = { error -> error.toProblem() },
                            transformSuccess = { schoolClass ->
                                ClassResponse.fromClass(schoolClass)
                            }
                        )
                    }
                    route("/lectures") {
                        get {
                            val limit = call.parameters["limit"]
                            val offset = call.parameters["offset"]
                            val id = call.parameters["classId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
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
            }
        }
    }
}