package main

import main.commands.Commands
import main.storage.TaskStore

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        Commands.help()
        return
    }

    val store = TaskStore()
    val command = args[0].lowercase()

    when (command) {
        "add" -> {
            if (args.size < 2) {
                println("Error: 'add' requires a task title.")
                println("Usage: focusboard add <title>")
                return
            }
            val title = args.drop(1).joinToString(" ")
            Commands.add(store, title)
        }
        "list" -> Commands.list(store)
        "done" -> {
            if (args.size < 2) {
                println("Error: 'done' requires a task id.")
                println("Usage: focusboard done <id>")
                return
            }
            Commands.done(store, args[1])
        }
        "search" -> {
            if (args.size < 2) {
                println("Error: 'search' requires a query.")
                println("Usage: focusboard search <query>")
                return
            }
            val query = args.drop(1).joinToString(" ")
            Commands.search(store, query)
        }
        "delete" -> {
            if (args.size < 2) {
                println("Error: 'delete' requires a task id.")
                println("Usage: focusboard delete <id>")
                return
            }
            Commands.delete(store, args[1])
        }
        "help", "--help", "-h" -> Commands.help()
        else -> {
            println("Unknown command: $command")
            Commands.help()
        }
    }
}
