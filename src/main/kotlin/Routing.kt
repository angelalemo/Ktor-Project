package com.naruedon672110147

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.Serializable

enum class Status {
    OPEN,
    IN_PROGRESS,
    CLOSED
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

@Serializable
data class Issue(
    val id: Int,
    val title: String,
    val description: String,
    val status: Status,
    val priority: Priority
)

@Serializable
data class IssuesRequest(val title: String,val description: String,val status: Status,val priority: Priority)

object IssuesRepository{
    private val issues = mutableListOf<Issue>(
        Issue(1, "Issue 1", "Description 1", Status.CLOSED, Priority.HIGH),
        Issue(2, "Issue 2", "Description 2", Status.IN_PROGRESS, Priority.MEDIUM),
        Issue(3, "Issue 3", "Description 3", Status.OPEN, Priority.LOW),
    )

    private var nextId = 4

    fun getById(id: Int): Issue? {
        return issues.find { it.id == id }
    }

    fun add(issueRequest: IssuesRequest): Issue {
        val issue = Issue(
            id = nextId++,
            title = issueRequest.title,
            description = issueRequest.description,
            status = issueRequest.status,
            priority = issueRequest.priority
        )
        issues.add(issue)
        return issue
    }

    fun update(id: Int, updatedIssue: Issue): Boolean {
        val index = issues.indexOfFirst { it.id == id }
        return if (index != -1) {
            issues[index] = updatedIssue
            true
        } else {
            false
        }
    }

    fun filter(status: Status?, priority: Priority?): List<Issue> {
        return issues.filter {
            (status == null || it.status == status) &&
            (priority == null || it.priority == priority)
        }
    }

    fun delete(id: Int): Boolean {
        return issues.removeIf { it.id == id }
    }
}


fun Application.configureRouting() {
    routing {

        //  /Issues → ดึงทั้งหมด
        //  /Issues?status=OPEN → เฉพาะที่ status = OPEN
        //  /Issues?priority=LOW → เฉพาะที่ priority = LOW
        //  /Issues?status=IN_PROGRESS&priority=HIGH → ทั้งสองเงื่อนไข คือ status=IN_PROGRESS และ priority=HIGH
        get("/Issues") {
            val statusParam = call.request.queryParameters["status"]
            val priorityParam = call.request.queryParameters["priority"]

            val status = try {
                statusParam?.uppercase()?.let { Status.valueOf(it) }
            } catch (e: IllegalArgumentException) {
                null // ถ้าผู้ใช้พิมพ์ status ผิด เช่น status=opened → จะเป็น null
            }

            val priority = try {
                priorityParam?.uppercase()?.let { Priority.valueOf(it) }
            } catch (e: IllegalArgumentException) {
                null // เช่น priority=medium-high → null
            }

            // หากกรองไม่ได้ทั้งสองอย่างเลย ให้ตอบ BadRequest
            if (statusParam != null && status == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid status value: $statusParam")
                return@get
            }

            if (priorityParam != null && priority == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid priority value: $priorityParam")
                return@get
            }

            val filtered = IssuesRepository.filter(status, priority)
            call.respond(HttpStatusCode.OK, filtered)
        }

        get("/Issues/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@get
            }

            val issuebyid = IssuesRepository.getById(id)
            if (issuebyid == null) {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            } else {
                call.respond(HttpStatusCode.OK, issuebyid)
            }
        }

        post("/Issues") {
            val request = call.receive<IssuesRequest>()
            val created = IssuesRepository.add(request)
            call.respond(HttpStatusCode.Created, created)
        }

        put("/Issues/{id}/status") {
            val id = call.parameters["id"]?.toIntOrNull()
            val statusRequest = call.receive<Map<String, String>>() // {"status": "IN_PROGRESS"}
            val newStatus = statusRequest["status"]?.let { Status.valueOf(it) }

            if (id != null && newStatus != null) {
                val issue = IssuesRepository.getById(id)
                if (issue != null) {
                    val updated = issue.copy(status = newStatus)
                    IssuesRepository.update(id, updated)
                    call.respond(HttpStatusCode.OK, updated)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Issue not found")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID or Status")
            }
        }

        delete("/Issues/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID format")
                return@delete
            }
            val isDeleted = IssuesRepository.delete(id)
            if (isDeleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "Issues not found")
            }

        }
    }
}

