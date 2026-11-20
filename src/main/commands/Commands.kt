package main.commands

import main.model.Task
import main.storage.TaskStore

object Commands {

    fun add(store: TaskStore, title: String) {
        if (title.isBlank()) {
            println("Error: Task title cannot be empty.")
            return
        }
        val task = Task.create(title)
        store.addTask(task)
        println("Added task: \"$title\" (id: ${task.id})")
    }

    fun list(store: TaskStore) {
        val tasks = store.load()
        if (tasks.isEmpty()) {
            println("No tasks found.")
            return
        }
        tasks.forEachIndexed { index, task ->
            val status = if (task.done) "✓" else "○"
            println("  ${index + 1}. [$status] [${task.id}] ${task.title}")
        }
        println("\nTotal: ${tasks.size} task(s)")
    }

    fun done(store: TaskStore, id: String) {
        val result = store.markDone(id)
        if (result == null) {
            println("Error: Task with id '$id' not found.")
        } else {
            val task = result.find { it.id == id }
            println("Marked done: \"${task?.title}\"")
        }
    }

    fun search(store: TaskStore, query: String) {
        if (query.isBlank()) {
            println("Error: Search query cannot be empty.")
            return
        }
        val results = store.search(query)
        if (results.isEmpty()) {
            println("No tasks matching \"$query\".")
        } else {
            println("Search results for \"$query\":")
            results.forEach { task ->
                val status = if (task.done) "✓" else "○"
                println("  [$status] [${task.id}] ${task.title}")
            }
            println("\nFound: ${results.size} task(s)")
        }
    }

    fun delete(store: TaskStore, id: String) {
        val result = store.deleteTask(id)
        if (result == null) {
            println("Error: Task with id '$id' not found.")
        } else {
            println("Deleted task with id '$id'.")
        }
    }

    fun help() {
        println("""
            FocusBoard - A simple local task tracker

            Usage: focusboard <command> [arguments]

            Commands:
              add <title>       Add a new task
              list              List all tasks
              done <id>         Mark a task as done
              search <query>    Search tasks by text
              delete <id>       Delete a task by id
              help              Show this help message

            Examples:
              focusboard add "Buy groceries"
              focusboard list
              focusboard done abc1234
              focusboard search "groceries"
              focusboard delete abc1234
        """.trimIndent())
    }
}
