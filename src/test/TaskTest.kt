package test

import main.model.Task

class TaskTest {

    companion object {
        val suite = TestSuite("TaskTest", listOf(
            TestEntry("given id and title when create task then task has correct properties") {
                val task = Task.create("1", "Buy groceries")

                assertEquals("1", task.id)
                assertEquals("Buy groceries", task.title)
                assertFalse(task.done)
                assertTrue(task.createdAt > 0)
            },
            TestEntry("given different ids when create two tasks then ids differ") {
                val task1 = Task.create("1", "Task 1")
                val task2 = Task.create("2", "Task 2")

                assertTrue(task1.id != task2.id)
            }
        ))
    }
}
