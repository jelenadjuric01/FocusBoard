package main.storage

import main.model.Task
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class TaskStore(private val filePath: String = "tasks.json") {

    private val file = File(filePath)

    fun load(): List<Task> {
        if (!file.exists()) return emptyList()
        val json = try {
            file.readText().trim()
        } catch (e: Exception) {
            throw TaskStoreException("Failed to read $filePath: ${e.message}")
        }
        if (json.isEmpty()) return emptyList()
        return parseTasks(json)
    }

    fun save(tasks: List<Task>) {
        val json = serializeTasks(tasks)
        val tempFile = File("${filePath}.tmp")
        try {
            tempFile.writeText(json)
            Files.move(tempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
            tempFile.delete()
            throw TaskStoreException("Failed to write $filePath: ${e.message}")
        }
    }

    fun addTask(title: String): Task {
        val tasks = load()
        val nextId = (tasks.maxOfOrNull { it.id.toInt() } ?: 0) + 1
        val task = Task.create(nextId.toString(), title)
        val updated = tasks + task
        save(updated)
        return task
    }

    fun markDone(id: String): MarkDoneResult {
        val tasks = load()
        val index = tasks.indexOfFirst { it.id == id }
        if (index == -1) return MarkDoneResult.NotFound
        if (tasks[index].done) return MarkDoneResult.AlreadyDone
        val updated = tasks.toMutableList()
        updated[index] = tasks[index].copy(done = true)
        save(updated)
        return MarkDoneResult.Done(updated[index])
    }

    fun deleteTask(id: String): DeleteResult {
        val tasks = load()
        val filtered = tasks.filterNot { it.id == id }
        if (filtered.size == tasks.size) return DeleteResult.NotFound
        save(filtered)
        return DeleteResult.Deleted
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

    /**
     * Parse JSON respecting string boundaries — handles } inside strings.
     * Extracts each JSON object by tracking brace depth outside of strings.
     */
    private fun parseTasks(json: String): List<Task> {
        val trimmed = json.trim()
        if (trimmed == "[]") return emptyList()

        val tasks = mutableListOf<Task>()
        val idRegex = Regex(""""id"\s*:\s*"((?:[^"\\]|\\.)*)""")
        val titleRegex = Regex(""""title"\s*:\s*"((?:[^"\\]|\\.)*)""")
        val doneRegex = Regex(""""done"\s*:\s*(true|false)""")
        val createdAtRegex = Regex(""""createdAt"\s*:\s*(\d+)""")

        // Split into individual JSON objects by tracking brace depth outside strings
        val objects = extractJsonObjects(trimmed)

        for (obj in objects) {
            val idMatch = idRegex.find(obj)
            val titleMatch = titleRegex.find(obj)
            val doneMatch = doneRegex.find(obj)
            val createdAtMatch = createdAtRegex.find(obj)

            if (idMatch != null && titleMatch != null && doneMatch != null && createdAtMatch != null) {
                tasks.add(Task(
                    id = unescapeJson(idMatch.groupValues[1]),
                    title = unescapeJson(titleMatch.groupValues[1]),
                    done = doneMatch.groupValues[1] == "true",
                    createdAt = createdAtMatch.groupValues[1].toLong()
                ))
            }
        }
        return tasks
    }

    /**
     * Extract individual JSON objects from a JSON array, respecting string boundaries.
     */
    private fun extractJsonObjects(json: String): List<String> {
        val objects = mutableListOf<String>()
        var i = 0
        while (i < json.length) {
            // Find opening brace
            val start = json.indexOf('{', i)
            if (start == -1) break

            // Find matching closing brace, respecting strings
            var depth = 0
            var inString = false
            var escape = false
            var end = -1

            for (j in start until json.length) {
                val c = json[j]
                if (escape) {
                    escape = false
                    continue
                }
                if (c == '\\' && inString) {
                    escape = true
                    continue
                }
                if (c == '"') {
                    inString = !inString
                    continue
                }
                if (inString) continue

                if (c == '{') depth++
                if (c == '}') {
                    depth--
                    if (depth == 0) {
                        end = j
                        break
                    }
                }
            }

            if (end != -1) {
                objects.add(json.substring(start, end + 1))
                i = end + 1
            } else {
                break
            }
        }
        return objects
    }

    private fun escapeJson(s: String): String {
        val sb = StringBuilder(s.length + 16)
        for (c in s) {
            when (c) {
                '\\' -> sb.append("\\\\")
                '"' -> sb.append("\\\"")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '\b' -> sb.append("\\b")
                '\u000C' -> sb.append("\\f")
                else -> {
                    if (c.code < 0x20) {
                        sb.append(String.format("\\u%04x", c.code))
                    } else {
                        sb.append(c)
                    }
                }
            }
        }
        return sb.toString()
    }

    private fun unescapeJson(s: String): String {
        val sb = StringBuilder(s.length)
        var i = 0
        while (i < s.length) {
            when (val c = s[i]) {
                '\\' -> {
                    if (i + 1 >= s.length) {
                        sb.append(c)
                        i++
                    } else {
                        val next = s[i + 1]
                        when (next) {
                            '"' -> sb.append('"')
                            '\\' -> sb.append('\\')
                            '/' -> sb.append('/')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'u' -> {
                                if (i + 5 < s.length) {
                                    val hex = s.substring(i + 2, i + 6)
                                    try {
                                        val codePoint = hex.toInt(16)
                                        sb.append(codePoint.toChar())
                                        i += 5
                                    } catch (e: NumberFormatException) {
                                        sb.append(c)
                                    }
                                } else {
                                    sb.append(c)
                                }
                            }
                            else -> {
                                sb.append(c)
                                sb.append(next)
                            }
                        }
                        i += 2
                    }
                }
                else -> {
                    sb.append(c)
                    i++
                }
            }
        }
        return sb.toString()
    }
}

sealed class MarkDoneResult {
    data class Done(val task: Task) : MarkDoneResult()
    object AlreadyDone : MarkDoneResult()
    object NotFound : MarkDoneResult()
}

sealed class DeleteResult {
    object Deleted : DeleteResult()
    object NotFound : DeleteResult()
}

class TaskStoreException(message: String) : RuntimeException(message)
