import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.*;
import utils.Managers;

import java.util.ArrayList;


class InMemoryTaskManagerTest {
    private InMemoryTaskManager tm;

    @BeforeEach
    void createInMemoryTaskManager() {
        tm = (InMemoryTaskManager) Managers.getDefault();
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
    void testCreateEpicTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final ArrayList<EpicTask> list = tm.getAllEpicTask();

        assertNotNull(list, "Список с EpicTask не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во EpicTask");
        EpicTask savedEpicTask = list.getLast();
        assertEquals(epicTask.getName(), savedEpicTask.getName(), "name не совпадают");
        assertEquals(epicTask.getDescription(), savedEpicTask.getDescription(), "description не совпадают");
        assertEquals(epicTask.getStatus(), savedEpicTask.getStatus(), "status не совпадают");
        assertArrayEquals(
                epicTask.getSubTasks().toArray(),
                savedEpicTask.getSubTasks().toArray(),
                "subTasks не совпадают"
        );
    }

    @Test
    void testGetTaskByID() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);

        assertNotNull(savedTask, "Task не был найден");
        assertEquals(task, savedTask, "Task не совпадают");
    }

    @Test
    void testGetSubTaskByID() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("c", "d", epicTask);
        final int subTask_id = tm.createSubTask(subTask);
        subTask.setId(subTask_id);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);

        assertNotNull(savedSubTask, "SubTask не был найден");
        assertEquals(subTask, savedSubTask, "SubTask не совпадают");
    }

    @Test
    void testGetEpicTaskByID() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final EpicTask savedEpicTask = tm.getEpicTaskByID(epicTask_id);

        assertNotNull(savedEpicTask, "EpicTask не был найден");
        assertEquals(epicTask, savedEpicTask, "EpicTask не совпадают");
    }

    @Test
    void testTheIncomingTaskDiffersFromTheSavedOne() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);

        assertFalse(task == savedTask);
    }

    @Test
    void testTheIncomingSubTaskDiffersFromTheSavedOne() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask);
        final int subTask_id = tm.createSubTask(subTask);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);

        assertFalse(subTask == savedSubTask);
    }

    @Test
    void testTheIncomingEpicTaskDiffersFromTheSavedOne() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final EpicTask savedEpicTask = tm.getEpicTaskByID(epicTask_id);

        assertFalse(epicTask == savedEpicTask);
    }


    @Test
    void testGetHistory() {
        assertNotNull(tm.getHistory(), "getHistory не выдает список");
    }


    @Test
    void testGetAllSubTaskByEpicTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        assertNotNull(tm.getAllSubTaskByEpicTask(epicTask_id));
    }

    @Test
    void testDeleteTaskByID() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);

        assertTrue(tm.deleteTaskByID(task_id), "Менеджер сообщает, что удаление не произошло");
        assertNull(tm.getTaskByID(task_id), "Task не был удален");
    }

    @Test
    void testDeleteSubTaskByID() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask);
        final int subTask_id = tm.createSubTask(subTask);

        assertTrue(tm.deleteSubTaskByID(subTask_id), "Менеджер сообщает, что удаление не произошло");
        assertNull(tm.getSubTaskByID(subTask_id), "SubTask не был удален");
        assertTrue(tm.getAllSubTaskByEpicTask(epicTask_id).isEmpty(), "SubTask не был удален из EpicTask");
    }

    @Test
    void testDeleteEpicTaskByID() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);

        assertTrue(tm.deleteEpicTaskByID(epicTask_id), "Менеджер сообщает, что удаление не произошло");
        assertNull(tm.getEpicTaskByID(epicTask_id), "EpicTask не был удален");
    }

    @Test
    void testClearTasks() {
        Task task = new Task("a", "b");
        tm.createTask(task);
        tm.clearTasks();

        assertTrue(tm.getAllTask().isEmpty(), "Tasks не был очищен");
    }

    @Test
    void testClearSubTasks() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask);
        tm.createSubTask(subTask);
        tm.clearSubTasks();

        assertTrue(tm.getAllSubTask().isEmpty(), "SubTasks не был очищен");
    }

    @Test
    void testClearEpicTasks() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        tm.clearEpicTasks();

        assertTrue(tm.getAllEpicTask().isEmpty(), "EpicTasks не был очищен");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);
        savedTask.setName("c");
        assertTrue(tm.updateTask(savedTask), "Менеджер сообщает, что Task не был обновлен");
        assertEquals(savedTask.getName(), tm.getTaskByID(task_id).getName(), "Task не был обновлен");
    }

    @Test
    void testUpdateSubTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask);
        final int subTask_id = tm.createSubTask(subTask);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);
        savedSubTask.setName("c");
        assertTrue(tm.updateSubTask(savedSubTask), "Менеджер сообщает, что SubTask не был обновлен");
        assertEquals(savedSubTask.getName(), tm.getSubTaskByID(subTask_id).getName(), "SubTask не был обновлен");
    }

    @Test
    void testUpdateEpicTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final EpicTask savedEpicTask = tm.getEpicTaskByID(epicTask_id);
        savedEpicTask.setName("c");
        assertTrue(tm.updateEpicTask(savedEpicTask), "Менеджер сообщает, что EpicTask не был обновлен");
        assertEquals(savedEpicTask.getName(), tm.getEpicTaskByID(epicTask_id).getName(), "EpicTask не был обновлен");
    }
}