import exception.NotFoundException;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T tm;

    @BeforeEach
    abstract void createTaskManager() throws Exception;

    @Test
    void testCreateTask() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final ArrayList<Task> list = new ArrayList<>(tm.getAllTask());

        assertNotNull(list, "Список с Task не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во Task");

        Task savedTask = list.getLast();
        assertEquals(task.getName(), savedTask.getName(), "name не совпадают");
        assertEquals(task.getDescription(), savedTask.getDescription(), "description не совпадают");
        assertEquals(task.getStatus(), savedTask.getStatus(), "status не совпадают");
        assertEquals(task.getStartTime(), savedTask.getStartTime(), "startTime не совпадают");
        assertEquals(task.getDuration(), savedTask.getDuration(), "duration не совпадают");
    }

    @Test
    void testCreateEpicTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        final List<EpicTask> list = tm.getAllEpicTask();

        assertNotNull(list, "Список с EpicTask не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во EpicTask");
        EpicTask savedEpicTask = list.getLast();
        assertEquals(epicTask.getName(), savedEpicTask.getName(), "name не совпадают");
        assertEquals(epicTask.getDescription(), savedEpicTask.getDescription(), "description не совпадают");
        assertEquals(epicTask.getStatus(), savedEpicTask.getStatus(), "status не совпадают");
        assertEquals(epicTask.getStartTime(), savedEpicTask.getStartTime(), "startTime не совпадают");
        assertEquals(epicTask.getDuration(), savedEpicTask.getDuration(), "duration не совпадают");
        assertArrayEquals(
                epicTask.getSubTasks().toArray(),
                savedEpicTask.getSubTasks().toArray(),
                "subTasks не совпадают"
        );
    }

    @Test
    void testCreateSubTask() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("c", "d", epicTask.getId());
        final int subTask_id = tm.createSubTask(subTask);
        subTask.setId(subTask_id);
        final List<SubTask> list = tm.getAllSubTask();

        assertNotNull(list, "Список с SubTask не возвращается");
        assertEquals(1, list.size(), "Неправильное кол-во SubTask");
        final SubTask savedSubTask = list.getLast();
        assertEquals(subTask.getName(), savedSubTask.getName(), "name не совпадают");
        assertEquals(subTask.getDescription(), savedSubTask.getDescription(), "description не совпадают");
        assertEquals(subTask.getStatus(), savedSubTask.getStatus(), "status не совпадают");
        assertEquals(subTask.getFkEpicTask(), savedSubTask.getFkEpicTask(), "fk_epicTask не совпадают");
        assertEquals(subTask.getStartTime(), savedSubTask.getStartTime(), "startTime не совпадают");
        assertEquals(subTask.getDuration(), savedSubTask.getDuration(), "duration не совпадают");
    }

    @Test
    void testClearTask() {
        Task task = new Task("a", "b");
        tm.createTask(task);
        tm.clearTasks();

        assertTrue(tm.getAllTask().isEmpty(), "Tasks не был очищен");
    }

    @Test
    void testClearEpicTask() {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        tm.clearEpicTasks();

        assertTrue(tm.getAllEpicTask().isEmpty(), "EpicTasks не был очищен");
    }

    @Test
    void testClearSubTask() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask.getId());
        tm.createSubTask(subTask);
        tm.clearSubTasks();

        assertTrue(tm.getAllSubTask().isEmpty(), "SubTasks не был очищен");
    }

    @Test
    void testGetTaskByID() throws NotFoundException {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);

        assertEquals(task, savedTask, "Task не совпадают");
    }

    @Test
    void testGetSubTaskByID() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("c", "d", epicTask.getId());
        final int subTask_id = tm.createSubTask(subTask);
        subTask.setId(subTask_id);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);

        assertNotNull(savedSubTask, "SubTask не был найден");
        assertEquals(subTask, savedSubTask, "SubTask не совпадают");
    }

    @Test
    void testGetEpicTaskByID() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final EpicTask savedEpicTask = tm.getEpicTaskByID(epicTask_id);

        assertNotNull(savedEpicTask, "EpicTask не был найден");
        assertEquals(epicTask, savedEpicTask, "EpicTask не совпадают");
    }

    @Test
    void testTheIncomingTaskDiffersFromTheSavedOne() throws NotFoundException {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);

        assertFalse(task == savedTask);
    }

    @Test
    void testTheIncomingSubTaskDiffersFromTheSavedOne() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask.getId());
        final int subTask_id = tm.createSubTask(subTask);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);

        assertFalse(subTask == savedSubTask);
    }

    @Test
    void testTheIncomingEpicTaskDiffersFromTheSavedOne() throws NotFoundException {
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
    void testGetAllSubTaskByEpicTask() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        assertNotNull(tm.getAllSubTaskByEpicTask(epicTask_id));
    }

    @Test
    void testDeleteTaskByID() {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);

        assertTrue(tm.deleteTaskByID(task_id), "Менеджер сообщает, что удаление не произошло");
        assertThrows(NotFoundException.class, () -> tm.getTaskByID(task_id), "Task не был удален");
    }

    @Test
    void testDeleteSubTaskByID() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask.getId());
        final int subTask_id = tm.createSubTask(subTask);

        assertTrue(tm.deleteSubTaskByID(subTask_id), "Менеджер сообщает, что удаление не произошло");
        assertThrows(NotFoundException.class, () -> tm.getSubTaskByID(subTask_id), "SubTask не был удален");
        assertTrue(tm.getAllSubTaskByEpicTask(epicTask_id).isEmpty(), "SubTask не был удален из EpicTask");
    }

    @Test
    void testDeleteEpicTaskByID() {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);

        assertTrue(tm.deleteEpicTaskByID(epicTask_id), "Менеджер сообщает, что удаление не произошло");
        assertThrows(NotFoundException.class, () -> tm.getEpicTaskByID(epicTask_id), "EpicTask не был удален");
    }

    @Test
    void testUpdateTask() throws NotFoundException {
        Task task = new Task("a", "b");
        final int task_id = tm.createTask(task);
        final Task savedTask = tm.getTaskByID(task_id);
        savedTask.setName("c");
        assertEquals(0, tm.updateTask(savedTask), "Менеджер сообщает, что Task не был обновлен");
        assertEquals(savedTask.getName(), tm.getTaskByID(task_id).getName(), "Task не был обновлен");
    }

    @Test
    void testUpdateSubTask() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        tm.createEpicTask(epicTask);
        SubTask subTask = new SubTask("a", "b", epicTask.getId());
        final int subTask_id = tm.createSubTask(subTask);
        final SubTask savedSubTask = tm.getSubTaskByID(subTask_id);
        savedSubTask.setName("c");
        assertEquals(0, tm.updateSubTask(savedSubTask), "Менеджер сообщает, что SubTask не был обновлен");
        assertEquals(savedSubTask.getName(), tm.getSubTaskByID(subTask_id).getName(), "SubTask не был обновлен");
    }

    @Test
    void testUpdateEpicTask() throws NotFoundException {
        EpicTask epicTask = new EpicTask("a", "b");
        final int epicTask_id = tm.createEpicTask(epicTask);
        final EpicTask savedEpicTask = tm.getEpicTaskByID(epicTask_id);
        savedEpicTask.setName("c");
        assertEquals(0, tm.updateEpicTask(savedEpicTask), "Менеджер сообщает, что EpicTask не был обновлен");
        assertEquals(savedEpicTask.getName(), tm.getEpicTaskByID(epicTask_id).getName(), "EpicTask не был обновлен");
    }

    @Test
    void testDeleteTaskFromHistory() throws NotFoundException {
        tm.createTask(new Task("a", "b"));
        tm.getTaskByID(0);

        assertFalse(tm.getHistory().isEmpty(), "История пуста");

        tm.deleteTaskByID(0);

        assertTrue(tm.getHistory().isEmpty(), "Task не была удалена");
    }

    @Test
    void testChangingTheStatusEpicAtAllNEW() throws NotFoundException {
        tm.createEpicTask(new EpicTask("a", "b"));
        tm.createSubTask(new SubTask("a", "b", 0));
        tm.createSubTask(new SubTask("a", "b", 0));

        assertEquals(Status.NEW, tm.getEpicTaskByID(0).getStatus(), "Статус должен быть NEW");
    }

    @Test
    void testChangingTheStatusEpicAtAllDONE() throws NotFoundException {
        tm.createEpicTask(new EpicTask("a", "b"));
        tm.createSubTask(new SubTask("a", "b", 0));
        tm.createSubTask(new SubTask("a", "b", 0));

        SubTask subTask1 = tm.getSubTaskByID(1);
        SubTask subTask2 = tm.getSubTaskByID(2);

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);

        tm.updateSubTask(subTask1);
        tm.updateSubTask(subTask2);

        assertEquals(Status.DONE, tm.getEpicTaskByID(0).getStatus(), "Статус должен быть DONE");
    }

    @Test
    void testChangingTheStatusEpicWithInProgress() throws NotFoundException {
        tm.createEpicTask(new EpicTask("a", "b"));
        tm.createSubTask(new SubTask("a", "b", 0));
        tm.createSubTask(new SubTask("a", "b", 0));

        SubTask subTask1 = tm.getSubTaskByID(1);

        subTask1.setStatus(Status.IN_PROGRESS);

        tm.updateSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, tm.getEpicTaskByID(0).getStatus(), "Статус должен быть IN_PROGRESS");
    }

    @Test
    void testChangingTheStatusEpicWithDoneAndNew() throws NotFoundException {
        tm.createEpicTask(new EpicTask("a", "b"));
        tm.createSubTask(new SubTask("a", "b", 0));
        tm.createSubTask(new SubTask("a", "b", 0));

        SubTask subTask1 = tm.getSubTaskByID(1);
        SubTask subTask2 = tm.getSubTaskByID(2);

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.NEW);

        tm.updateSubTask(subTask1);
        tm.updateSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, tm.getEpicTaskByID(0).getStatus(), "Статус должен быть IN_PROGRESS");
    }

    @Test
    void testGetPrioritizedTasksNoIntersection() {
        tm.createTask(new Task(
                        "a",
                        "b",
                        LocalDateTime.of(2025, 8, 6, 12, 0),
                        Duration.ofMinutes(40)
                )
        );

        tm.createTask(new Task(
                        "c",
                        "d",
                        LocalDateTime.of(2025, 8, 6, 13, 0),
                        Duration.ofMinutes(30)
                )
        );
        List<AbstractTask<?>> list = tm.getPrioritizedTasks();

        assertNotNull(list, "Функция выдала null, хотя ожидали пустой List");
        assertEquals(2, list.size(), "Задачи не должны были пересечься");
    }

    @Test
    void testGetPrioritizedTasksIntersection() {
        tm.createTask(new Task(
                        "a",
                        "b",
                        LocalDateTime.of(2025, 8, 6, 12, 0),
                        Duration.ofMinutes(40)
                )
        );

        tm.createTask(new Task(
                        "c",
                        "d",
                        LocalDateTime.of(2025, 8, 6, 12, 35),
                        Duration.ofMinutes(30)
                )
        );
        List<AbstractTask<?>> list = tm.getPrioritizedTasks();

        assertNotNull(list, "Функция выдала null, хотя ожидали пустой List");
        assertEquals(1, list.size(), "Задачи должны были пересечься");
    }
}
