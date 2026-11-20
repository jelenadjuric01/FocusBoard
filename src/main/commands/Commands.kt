package main.commands

import main.storage.DeleteResult
import main.storage.MarkDoneResult
import main.storage.TaskStore

object Commands {

    fun add(store: TaskStore, title: String) {
        if (title.isBlank()) {
            System.err.println("Error: Task title cannot be empty.")
            return
        }
        val task = store.addTask(title)
        println("Added task: \"$title\" (id: ${task.id})")
    }

    fun list(store: TaskStore) {
        val tasks = store.load()
        if (tasks.isEmpty()) {
            println("No tasks found.")
            return
        }
        tasks.forEach { task ->
            val status = if (task.done) "✓" else "○"
            println("  [$status] [${task.id}] ${task.title}")
        }
        println("\nTotal: ${tasks.size} task(s)")
    }

    fun done(store: TaskStore, id: String): Boolean {
        return when (val result = store.markDone(id)) {
            is MarkDoneResult.Done -> {
                println("Marked done: \"${result.task.title}\"")
                true
            }
            is MarkDoneResult.AlreadyDone -> {
                println("Task '$id' is already done.")
                true
            }
            is MarkDoneResult.NotFound -> {
                System.err.println("Error: Task with id '$id' not found.")
                false
            }
        }
    }

    fun delete(store: TaskStore, id: String): Boolean {
        return when (val result = store.deleteTask(id)) {
            is DeleteResult.Deleted -> {
                println("Deleted task with id '$id'.")
                true
            }
            is DeleteResult.NotFound -> {
                System.err.println("Error: Task with id '$id' not found.")
                false
            }
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
              delete <id>       Delete a task by id
              help              Show this help message

            Examples:
              focusboard add "Buy groceries"
              focusboard list
              focusboard done 1
              focusboard delete 1
        """.trimIndent())
    }
}
