package isel.leic.group25.api.oauth2

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import isel.leic.group25.api.oauth2.domain.MicrosoftUserInfo
import isel.leic.group25.api.oauth2.domain.Organization

suspend fun getMicrosoftUserInfo(accessToken: String, client: HttpClient): MicrosoftUserInfo? {
    val response = client.get("https://graph.microsoft.com/v1.0/me") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $accessToken")
            append(HttpHeaders.Accept, "application/json")
        }
    }
    if (response.status != HttpStatusCode.OK) {
        return null
    }
    val responseString = response.bodyAsText()
    val username = responseString.substringAfter("\"displayName\":\"").substringBefore("\"")
    val email = responseString.substringAfter("\"mail\":\"").substringBefore("\"")
    val userInfo = MicrosoftUserInfo(
        email = email,
        displayName = username
    )
    return userInfo
}

suspend fun getMicrosoftOrganizationInfo(accessToken: String, client: HttpClient): Organization? {
    val response = client.get("https://graph.microsoft.com/v1.0/organization") {
        headers {
            append(HttpHeaders.Authorization, "Bearer $accessToken")
            append(HttpHeaders.Accept, "application/json")
        }
    }
    if (response.status != HttpStatusCode.OK) {
        return null
    }
    val responseString = response.bodyAsText()
    val organizationName = responseString.substringAfter("\"displayName\":\"").substringBefore("\"")
    val organizationId = responseString.substringAfter("\"id\":\"").substringBefore("\"")
    val organizationInfo = Organization(
        id = organizationId,
        name = organizationName
    )
    return organizationInfo
}