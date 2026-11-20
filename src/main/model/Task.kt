package main.model

data class Task(
    val id: String,
    val title: String,
    val done: Boolean,
    val createdAt: Long
) {
    companion object {
        fun create(id: String, title: String): Task = Task(
            id = id,
            title = title,
            done = false,
            createdAt = System.currentTimeMillis()
        )
    }
}
