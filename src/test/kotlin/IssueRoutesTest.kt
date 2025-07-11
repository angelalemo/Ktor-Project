package com.naruedon672110147

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class IssueRoutesTest {

    @Test
    fun testGetAllIssues() = testApplication {
        application { module() }

        val response = client.get("/Issues")
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("Issue 1"))
    }

    @Test
    fun testGetIssueByIdSuccess() = testApplication {
        application { module() }

        val response = client.get("/Issues/1")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Issue 1"))
    }

    @Test
    fun testGetIssueByIdNotFound() = testApplication {
        application { module() }

        val response = client.get("/Issues/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testFilterIssuesByStatus() = testApplication {
        application { module() }

        val response = client.get("/Issues?status=OPEN")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("OPEN"))
    }

    @Test
    fun testFilterIssuesByInvalidStatus() = testApplication {
        application { module() }

        val response = client.get("/Issues?status=INVALID")
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testAddNewIssue() = testApplication {
        application { module() }

        val newIssue = IssuesRequest(
            title = "New Issue",
            description = "New Description",
            status = Status.OPEN,
            priority = Priority.HIGH
        )
        val response = client.post("/Issues") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(newIssue))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("New Issue"))
    }

    @Test
    fun testUpdateIssueStatus() = testApplication {
        application { module() }

        val response = client.put("/Issues/1/status") {
            contentType(ContentType.Application.Json)
            setBody("""{"status": "IN_PROGRESS"}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("IN_PROGRESS"))
    }

    @Test
    fun testUpdateIssueStatusInvalid() = testApplication {
        application { module() }

        val response = client.put("/Issues/1/status") {
            contentType(ContentType.Application.Json)
            setBody("""{"status": "INVALID"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun testDeleteIssueSuccess() = testApplication {
        application { module() }

        val response = client.delete("/Issues/1")
        assertEquals(HttpStatusCode.NoContent, response.status)
    }

    @Test
    fun testDeleteIssueNotFound() = testApplication {
        application { module() }

        val response = client.delete("/Issues/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}
