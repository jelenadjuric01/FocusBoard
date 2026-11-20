package main.model

import java.util.UUID

data class Task(
    val id: String,
    val title: String,
    val done: Boolean,
    val createdAt: Long
) {
    companion object {
        fun create(title: String): Task = Task(
            id = UUID.randomUUID().toString().take(8),
            title = title,
            done = false,
            createdAt = System.currentTimeMillis()
        )
    }
}
