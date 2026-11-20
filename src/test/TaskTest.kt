package test

import main.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskTest {

    @Test
    fun `given title when create task then task has correct properties`() {
        val task = Task.create("Buy groceries")

        assertEquals("Buy groceries", task.title)
        assertFalse(task.done)
        assertTrue(task.id.length == 8)
        assertTrue(task.createdAt > 0)
    }

    @Test
    fun `given title when create two tasks then ids are unique`() {
        val task1 = Task.create("Task 1")
        val task2 = Task.create("Task 2")

        assertTrue(task1.id != task2.id)
    }
}
