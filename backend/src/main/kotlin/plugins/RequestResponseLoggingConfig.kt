import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.request.contentType
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri
import io.ktor.util.AttributeKey
import org.slf4j.LoggerFactory

/**
 * Configuration for the RequestResponseLogging plugin
 */
class RequestResponseLoggingConfig {
    var includeHeaders: Boolean = false
    var logRequestBody: Boolean = false
}

/**
 * Simple plugin for logging requests and responses
 */
val RequestResponseLogging = createApplicationPlugin(
    name = "RequestResponseLogging",
    createConfiguration = ::RequestResponseLoggingConfig
) {
    val logger = LoggerFactory.getLogger("RequestResponseLogging")
    val startTimeKey = AttributeKey<Long>("RequestLogStartTime")

    // Log incoming requests at the very beginning
    application.intercept(ApplicationCallPipeline.Setup) {
        val startTime = System.currentTimeMillis()
        call.attributes.put(startTimeKey, startTime)

        val requestInfo = buildString {
            append("${call.request.httpMethod.value} ${call.request.uri}")

            if (this@createApplicationPlugin.pluginConfig.includeHeaders) {
                val headers = call.request.headers.entries()
                    .joinToString(", ") { "${it.key}: ${it.value.joinToString()}" }
                if (headers.isNotEmpty()) {
                    append(" | $headers")
                }
            }

            call.request.contentType()?.let { contentType ->
                append(" | Content-Type: $contentType")
            }
        }

        logger.info("→ $requestInfo")
    }

    // Log responses after they're sent
    application.intercept(ApplicationCallPipeline.Fallback) {
        try {
            val startTime = call.attributes.getOrNull(startTimeKey) ?: System.currentTimeMillis()
            val processingTime = System.currentTimeMillis() - startTime
            val status = call.response.status()?.value ?: 200
            val statusText = call.response.status()?.description ?: "OK"

            if (status >= 400) {
                logger.error("← $status $statusText | ${processingTime}ms")
            } else {
                logger.info("← $status $statusText | ${processingTime}ms")
            }
        } catch (cause: Throwable) {
            val startTime = call.attributes.getOrNull(startTimeKey) ?: System.currentTimeMillis()
            val processingTime = System.currentTimeMillis() - startTime
            logger.error("✗ ${cause::class.simpleName}: ${cause.message} | ${processingTime}ms")
            throw cause
        }
    }
}