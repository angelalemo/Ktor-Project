package com.naruedon672110147

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
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
data class Issues(
    val id: Int,
    val title: String,
    val description: String,
    val status: Status,
    val priority: Priority
)

@Serializable
data class IssuesRequest(val title: String,val description: String,val status: Status,val priority: Priority)

object IssuesRepository{
    private val issues = mutableListOf<Issues>(

    )

    //getAll(),  ดึงข้อมูล tasks ทั้งหมด
    fun getAll(): List<Issues>{
        return issues.toList()
    }
    //getById(id: Int), ดึงข้อมูล tasks by id
    fun getById(id: Int): Issues? {
        return issues.find { it.id == id }
    }
    //add(task: Task),  เพิ่มข้อมูล task เข้าไป
    fun add(task: Issues){
        issues.add(task)
    }
    //update(id: Int, updatedTask: Task),  update ข้อมูล task ตาม id
    fun update(id: Int, updatedTask: Issues): Boolean {
        val index = issues.indexOfFirst { it.id == id }
        return if (index != -1) {
            issues[index] = updatedTask
            true
        } else {
            false
        }
    }
    //delete(id: Int)   ลบข้อมูล task จาก id
    fun delete(id: Int): Boolean {
        return issues.removeIf { it.id == id }
    }
}


fun Application.configureRouting() {
    routing {


    }
}

