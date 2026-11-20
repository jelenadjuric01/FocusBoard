package main.storage

import main.model.Task
import java.io.File

class TaskStore(private val filePath: String = "tasks.json") {

    private val file get() = File(filePath)

    fun load(): List<Task> {
        if (!file.exists()) return emptyList()
        val json = file.readText().trim()
        if (json.isEmpty()) return emptyList()
        return parseTasks(json)
    }

    fun save(tasks: List<Task>) {
        val json = serializeTasks(tasks)
        file.writeText(json)
    }

    fun addTask(task: Task): List<Task> {
        val tasks = load() + task
        save(tasks)
        return tasks
    }

    fun markDone(id: String): List<Task>? {
        val tasks = load()
        val updated = tasks.map { if (it.id == id) it.copy(done = true) else it }
        if (updated == tasks) return null // not found
        save(updated)
        return updated
    }

    fun deleteTask(id: String): List<Task>? {
        val tasks = load()
        val filtered = tasks.filterNot { it.id == id }
        if (filtered.size == tasks.size) return null // not found
        save(filtered)
        return filtered
    }

    fun search(query: String): List<Task> {
        val lower = query.lowercase()
        return load().filter { it.title.lowercase().contains(lower) }
    }

    private fun serializeTasks(tasks: List<Task>): String {
        if (tasks.isEmpty()) return "[]"
        val lines = tasks.map { task ->
            "  {\n" +
                    "    \"id\": \"${escapeJson(task.id)}\",\n" +
                    "    \"title\": \"${escapeJson(task.title)}\",\n" +
                    "    \"done\": ${task.done},\n" +
                    "    \"createdAt\": ${task.createdAt}\n" +
                    "  }"
        }
        return "[\n" + lines.joinToString(",\n") + "\n]"
    }

    private fun parseTasks(json: String): List<Task> {
        val trimmed = json.trim()
        if (trimmed == "[]") return emptyList()

        val tasks = mutableListOf<Task>()
        val taskRegex = Regex("""\{\s*"id"\s*:\s*"([^"]*)"\s*,\s*"title"\s*:\s*"([^"]*)"\s*,\s*"done"\s*:\s*(true|false)\s*,\s*"createdAt"\s*:\s*(\d+)\s*\}""")

        for (match in taskRegex.findAll(trimmed)) {
            tasks.add(Task(
                id = match.groupValues[1],
                title = unescapeJson(match.groupValues[2]),
                done = match.groupValues[3] == "true",
                createdAt = match.groupValues[4].toLong()
            ))
        }
        return tasks
    }

    private fun escapeJson(s: String): String = s
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

    private fun unescapeJson(s: String): String = s
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
        .replace("\\\"", "\"")
        .replace("\\\\", "\\")
}
