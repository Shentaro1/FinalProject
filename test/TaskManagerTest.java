import managers.FileBackedTaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import utils.Managers;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class TaskManagerTest <T extends TaskManager> {
    private T tm;

    @BeforeEach
    abstract void createInMemoryTaskManager();

    @Test
    void testCreateSubTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("c", "d", epicTask);
        final int subTask_id = tm.createSubTask(subTask);
        subTask.setId(subTask_id);
        final ArrayList<SubTask> list = tm.getAllSubTask();

        assertNotNull(list, "Список с SubTask не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во SubTask");
        final SubTask savedSubTask = list.getLast();
        assertEquals(subTask.getName(), savedSubTask.getName(), "name не совпадают");
        assertEquals(subTask.getDescription(), savedSubTask.getDescription(), "description не совпадают");
        assertEquals(subTask.getStatus(), savedSubTask.getStatus(), "status не совпадают");
        assertEquals(subTask.getFk_epicTask(), savedSubTask.getFk_epicTask(), "fk_epicTask не совпадают");
    }

    @Test
    void testCreateTask() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final ArrayList<Task> list = tm.getAllTask();

        assertNotNull(list, "Список с Task не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во Task");

        Task savedTask = list.getLast();
        assertEquals(task.getName(), savedTask.getName(), "name не совпадают");
        assertEquals(task.getDescription(), savedTask.getDescription(), "description не совпадают");
        assertEquals(task.getStatus(), savedTask.getStatus(), "status не совпадают");
    }

    @Test
    abstract void testCreateEpicTask();


    @Test
    abstract void testClearTasks();

    @Test
    abstract void testClearSubTask();

    @Test
    abstract void testClearEpicTask();

    @Test
    abstract void testUpdateTask();

    @Test
    abstract void testUpdateSubTask();

    @Test
    abstract void testUpdateEpicTask();



}
