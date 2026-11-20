package main

import main.commands.Commands
import main.storage.TaskStore
import main.storage.TaskStoreException
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    try {
        if (args.isEmpty()) {
            Commands.help()
            return
        }

        val store = TaskStore()
        val command = args[0].lowercase()

        when (command) {
            "add" -> {
                if (args.size < 2) {
                    System.err.println("Error: 'add' requires a task title.")
                    System.err.println("Usage: focusboard add <title>")
                    exitProcess(1)
                }
                val title = args.drop(1).joinToString(" ")
                Commands.add(store, title)
            }
            "list" -> Commands.list(store)
            "done" -> {
                if (args.size < 2) {
                    System.err.println("Error: 'done' requires a task id.")
                    System.err.println("Usage: focusboard done <id>")
                    exitProcess(1)
                }
                if (!Commands.done(store, args[1])) {
                    exitProcess(1)
                }
            }
            "delete" -> {
                if (args.size < 2) {
                    System.err.println("Error: 'delete' requires a task id.")
                    System.err.println("Usage: focusboard delete <id>")
                    exitProcess(1)
                }
                if (!Commands.delete(store, args[1])) {
                    exitProcess(1)
                }
            }
            "help", "--help", "-h" -> Commands.help()
            else -> {
                System.err.println("Unknown command: $command")
                Commands.help()
                exitProcess(1)
            }
        }
    } catch (e: TaskStoreException) {
        System.err.println("Error: ${e.message}")
        exitProcess(1)
    } catch (_: Exception) {
        System.err.println("Error: An unexpected error occurred.")
        exitProcess(1)
    }
}
