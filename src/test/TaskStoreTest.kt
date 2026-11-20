package test

import main.model.Task
import main.storage.*
import java.io.File

class TaskStoreTest {

    companion object {
        private var fileCounter = 0

        private fun createTempStore(): Pair<TaskStore, File> {
            fileCounter++
            val tempFile = File("test_tasks_${System.currentTimeMillis()}_$fileCounter.json")
            val store = TaskStore(tempFile.absolutePath)
            return store to tempFile
        }

        val suite = TestSuite("TaskStoreTest", listOf(
            TestEntry("given no file when load then returns empty list") {
                val (store, tempFile) = createTempStore()
                assertTrue(store.load().isEmpty())
                tempFile.delete()
            },

            TestEntry("given empty list when save and load then returns empty list") {
                val (store, tempFile) = createTempStore()
                store.save(emptyList())
                assertTrue(store.load().isEmpty())
                tempFile.delete()
            },

            TestEntry("given task when addTask then task is persisted with id 1") {
                val (store, tempFile) = createTempStore()
                val task = store.addTask("Test task")

                assertEquals("1", task.id)
                assertEquals("Test task", task.title)
                assertFalse(task.done)
                assertEquals(1, store.load().size)
                tempFile.delete()
            },

            TestEntry("given multiple tasks when add then ids are sequential") {
                val (store, tempFile) = createTempStore()
                val task1 = store.addTask("Task 1")
                val task2 = store.addTask("Task 2")
                val task3 = store.addTask("Task 3")

                assertEquals("1", task1.id)
                assertEquals("2", task2.id)
                assertEquals("3", task3.id)
                assertEquals(3, store.load().size)
                tempFile.delete()
            },

            TestEntry("given task when markDone then task is completed") {
                val (store, tempFile) = createTempStore()
                val task = store.addTask("To complete")

                val result = store.markDone(task.id)
                assertTrue(result is MarkDoneResult.Done)
                val doneTask = (result as MarkDoneResult.Done).task
                assertTrue(doneTask.done)
                assertEquals(task.id, doneTask.id)
                tempFile.delete()
            },

            TestEntry("given no matching id when markDone then returns NotFound") {
                val (store, tempFile) = createTempStore()
                val result = store.markDone("nonexistent")
                assertTrue(result is MarkDoneResult.NotFound)
                tempFile.delete()
            },

            TestEntry("given already done task when markDone then returns AlreadyDone") {
                val (store, tempFile) = createTempStore()
                val task = store.addTask("Already done")
                store.markDone(task.id)

                val result = store.markDone(task.id)
                assertTrue(result is MarkDoneResult.AlreadyDone)
                tempFile.delete()
            },

            TestEntry("given task when delete then task is removed") {
                val (store, tempFile) = createTempStore()
                val task = store.addTask("To delete")

                val result = store.deleteTask(task.id)
                assertTrue(result is DeleteResult.Deleted)
                val remaining = store.load().filter { it.id == task.id }
                assertTrue(remaining.isEmpty())
                tempFile.delete()
            },

            TestEntry("given no matching id when delete then returns NotFound") {
                val (store, tempFile) = createTempStore()
                val result = store.deleteTask("nonexistent")
                assertTrue(result is DeleteResult.NotFound)
                tempFile.delete()
            },

            TestEntry("given deleted task when markDone then returns NotFound") {
                val (store, tempFile) = createTempStore()
                val task = store.addTask("To delete")
                store.deleteTask(task.id)

                val result = store.markDone(task.id)
                assertTrue(result is MarkDoneResult.NotFound)
                tempFile.delete()
            },

            TestEntry("given task with special chars when save and load then title preserved") {
                val (store, tempFile) = createTempStore()
                store.addTask("Task with \"quotes\" and \\backslash")

                val loaded = store.load()
                assertEquals(1, loaded.size)
                assertEquals("Task with \"quotes\" and \\backslash", loaded[0].title)
                tempFile.delete()
            },

            TestEntry("given tasks with done flag when save and load then done flag preserved") {
                val (store, tempFile) = createTempStore()
                val task1 = Task.create("1", "Done task").copy(done = true)
                val task2 = Task.create("2", "Pending task")
                store.save(listOf(task1, task2))

                val loaded = store.load()
                assertEquals(2, loaded.size)
                assertTrue(loaded[0].done)
                assertFalse(loaded[1].done)
                tempFile.delete()
            },

            TestEntry("given task with backslash-n when save and load then title preserved") {
                val (store, tempFile) = createTempStore()
                store.addTask("C:\\new")

                val loaded = store.load()
                assertEquals(1, loaded.size)
                assertEquals("C:\\new", loaded[0].title)
                tempFile.delete()
            },

            TestEntry("given task with curly brace when save and load then title preserved") {
                val (store, tempFile) = createTempStore()
                store.addTask("fix {foo}")

                val loaded = store.load()
                assertEquals(1, loaded.size)
                assertEquals("fix {foo}", loaded[0].title)
                tempFile.delete()
            },

            TestEntry("given task with newline when save and load then title preserved") {
                val (store, tempFile) = createTempStore()
                store.addTask("Line1\nLine2")

                val loaded = store.load()
                assertEquals(1, loaded.size)
                assertEquals("Line1\nLine2", loaded[0].title)
                tempFile.delete()
            },

            TestEntry("given task with emoji when save and load then title preserved") {
                val (store, tempFile) = createTempStore()
                store.addTask("Buy 🍎 apples")

                val loaded = store.load()
                assertEquals(1, loaded.size)
                assertEquals("Buy 🍎 apples", loaded[0].title)
                tempFile.delete()
            },

            TestEntry("given tasks deleted when add new then id continues from max") {
                val (store, tempFile) = createTempStore()
                store.addTask("Task 1")
                store.addTask("Task 2")
                store.addTask("Task 3")
                store.deleteTask("2")

                val task4 = store.addTask("Task 4")
                assertEquals("4", task4.id)
                tempFile.delete()
            }
        ))
    }
}
