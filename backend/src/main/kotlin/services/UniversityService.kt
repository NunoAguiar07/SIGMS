package isel.leic.group25.services

import isel.leic.group25.db.entities.timetables.University
import isel.leic.group25.db.repositories.Repositories
import isel.leic.group25.db.repositories.interfaces.Transactionable
import isel.leic.group25.services.errors.UserClassError
import isel.leic.group25.utils.Either
import isel.leic.group25.utils.failure
import isel.leic.group25.utils.success
import java.sql.SQLException

typealias UniversityListResult = Either<UserClassError, List<University>>

class UniversityService(
    private val repositories: Repositories,
    private val transactionable: Transactionable
) {
    private inline fun <T> runCatching(block: () -> Either<UserClassError, T>): Either<UserClassError, T> {
        return try {
            block()
        } catch (e: SQLException) {
            failure(UserClassError.ConnectionDbError(e.message))
        }
    }

    fun getUniversitiesByName(limit:Int, offset:Int, search:String): UniversityListResult{
        return runCatching {
            transactionable.useTransaction {
                val universities = repositories.from({universityRepository}) {
                    getUniversitiesByName(limit, offset, search)
                }
                return@useTransaction success(universities)
            }
        }
    }

    fun getAllUniversities(limit: Int, offset: Int): UniversityListResult {
        return runCatching {
            transactionable.useTransaction {
                val universities = repositories.from({universityRepository}) {
                    getAllUniversities(limit, offset) }
                return@useTransaction success(universities)
            }
        }
    }
}