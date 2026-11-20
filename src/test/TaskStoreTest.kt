package test

import main.model.Task
import main.storage.TaskStore
import java.io.File
import kotlin.test.*

class TaskStoreTest {

    private val tempFile = File("test_tasks_${System.currentTimeMillis()}.json")
    private val store = TaskStore(tempFile.absolutePath)

    @AfterTest
    fun cleanup() {
        tempFile.delete()
    }

    @Test
    fun `given no file when load then returns empty list`() {
        val tasks = store.load()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `given empty list when save and load then returns empty list`() {
        store.save(emptyList())
        val tasks = store.load()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun `given task when addTask then task is persisted`() {
        val task = Task.create("Test task")
        val result = store.addTask(task)

        assertEquals(1, result.size)
        assertEquals(task.id, result[0].id)
        assertEquals(task.title, result[0].title)
        assertFalse(result[0].done)
    }

    @Test
    fun `given multiple tasks when add then all persisted`() {
        store.addTask(Task.create("Task 1"))
        store.addTask(Task.create("Task 2"))
        store.addTask(Task.create("Task 3"))

        val tasks = store.load()
        assertEquals(3, tasks.size)
    }

    @Test
    fun `given task when markDone then task is completed`() {
        val task = Task.create("To complete")
        store.addTask(task)

        val result = store.markDone(task.id)
        assertNotNull(result)
        val updated = result!!.find { it.id == task.id }
        assertNotNull(updated)
        assertTrue(updated!!.done)
    }

    @Test
    fun `given no matching id when markDone then returns null`() {
        val result = store.markDone("nonexistent")
        assertNull(result)
    }

    @Test
    fun `given task when delete then task is removed`() {
        val task = Task.create("To delete")
        store.addTask(task)

        val result = store.deleteTask(task.id)
        assertNotNull(result)
        val remaining = result!!.filter { it.id == task.id }
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun `given no matching id when delete then returns null`() {
        val result = store.deleteTask("nonexistent")
        assertNull(result)
    }

    @Test
    fun `given tasks when search matching query then returns matches`() {
        store.addTask(Task.create("Buy milk"))
        store.addTask(Task.create("Buy bread"))
        store.addTask(Task.create("Call mom"))

        val results = store.search("milk")
        assertEquals(1, results.size)
        assertEquals("Buy milk", results[0].title)
    }

    @Test
    fun `given tasks when search case insensitive then returns matches`() {
        store.addTask(Task.create("Buy Milk"))

        val results = store.search("milk")
        assertEquals(1, results.size)
    }

    @Test
    fun `given tasks when search no match then returns empty`() {
        store.addTask(Task.create("Buy milk"))

        val results = store.search("eggs")
        assertTrue(results.isEmpty())
    }

    @Test
    fun `given task with special chars when save and load then title preserved`() {
        val task = Task.create("Task with \"quotes\" and \\backslash")
        store.addTask(task)

        val loaded = store.load()
        assertEquals(1, loaded.size)
        assertEquals(task.title, loaded[0].title)
    }

    @Test
    fun `given tasks with done flag when save and load then done flag preserved`() {
        val task1 = Task.create("Done task").copy(done = true)
        val task2 = Task.create("Pending task")
        store.save(listOf(task1, task2))

        val loaded = store.load()
        assertEquals(2, loaded.size)
        assertTrue(loaded[0].done)
        assertFalse(loaded[1].done)
    }
}
