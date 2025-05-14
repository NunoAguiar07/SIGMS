package isel.leic.group25.api.oauth2

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import isel.leic.group25.api.oauth2.domain.MicrosoftUserInfo
import isel.leic.group25.api.oauth2.domain.Organization
import isel.leic.group25.api.oauth2.domain.OrganizationResponse

suspend fun getMicrosoftUserInfo(accessToken: String, client: HttpClient): MicrosoftUserInfo? {
    return try {
        client.get("https://graph.microsoft.com/v1.0/me") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.Accept, "application/json")
            }
        }.body()
    } catch (e: Exception) {
        null
    }
}

suspend fun getMicrosoftOrganizationInfo(accessToken: String, client: HttpClient): Organization? {
    return try {
        val response = client.get("https://graph.microsoft.com/v1.0/organization") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $accessToken")
                append(HttpHeaders.Accept, "application/json")
            }
        }.body<OrganizationResponse>()
        response.value.firstOrNull()
    } catch (e: Exception) {
        null
    }
}