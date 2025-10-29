import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import types.Status;
import utils.Managers;

import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    private ArrayList<String> readerFile() {
        ArrayList<String> result = new ArrayList<>();

        try (
                FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
                BufferedReader bf = new BufferedReader(reader);
        ) {
            while (bf.ready()) {
                result.add(bf.readLine());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @BeforeEach
    void createTaskManager() throws IOException {
        file = File.createTempFile("test", "csv");
        tm = (FileBackedTaskManager) Managers.getDefaultFileBack(file);
    }

    @Test
    void testCreateTask() {
        tm.createTask(new Task("a", "b", Status.NEW, 28, Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000)));
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,TASK,a,NEW,b,2019-03-28T14:33:48.000640,PT0.000000001S", bf.getLast(), "");
    }

    @Test
    void testCreateEpicTask() {
        tm.createEpicTask(new EpicTask("a", "b"));
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,EPICTASK,b,NEW,a,,", bf.getLast(), "");
    }

    @Test
    void testCreateSubTask() {
        super.testCreateSubTask();
        ArrayList<String> bf = readerFile();

        assertEquals(3, bf.size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,a,NEW,b,0,2019-03-28T14:33:48.000640,PT0.000000001S", bf.getLast(), "");
    }

    @Test
    void testCreateHeading() {
        tm.createTask(new Task("a", "b", Status.NEW, 28, Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000)));
        assertEquals(
                "id,type,name,status,description,epic,startTime,duration",
                readerFile().getFirst(),
                "Шапка неправильная"
        );
    }

    @Test
    void testClearTask() {
        tm.createTask(new Task("a", "b", Status.NEW, 28, Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000)));
        tm.clearTasks();
        assertEquals(1, readerFile().size(), "Tasks не были удалены");
    }

    @Test
    void testClearEpicTask() {
        tm.createEpicTask(new EpicTask("a", "b"));
        tm.clearEpicTasks();
        assertEquals(1, readerFile().size(), "EpicTasks не были удалены");
    }

    @Test
    void testClearSubTask() {
        int epicId = tm.createEpicTask(new EpicTask("a", "b"));
        tm.createSubTask(new SubTask("a", "b", Status.NEW, 1, tm.getEpicTaskByID(epicId) ,Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000)));
        tm.clearEpicTasks();
        tm.clearSubTasks();

        assertEquals(1, readerFile().size(), "SubTasks не были удалены");
    }

    @Test
    void testUpdateTask() {
        Task task = tm.getTaskByID(tm.createTask(new Task("a", "b", Status.NEW, 0, Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000))));
        task.setStatus(Status.DONE);
        tm.updateTask(task);
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,TASK,a,DONE,b,2019-03-28T14:33:48.000640,PT0.000000001S", bf.getLast(), "Task не был обновлен");
    }

    @Test
    void testUpdateEpicTask() {
        EpicTask epicTask = tm.getEpicTaskByID(tm.createEpicTask(new EpicTask("c", "b")));
        epicTask.setDescription("b");
        tm.updateEpicTask(epicTask);
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,EPICTASK,b,NEW,b,,", bf.getLast(), "EpicTask не был обновлен");
    }

    @Test
    void testUpdateSubTask() {
        int epicId = tm.createEpicTask(new EpicTask("a", "b"));
        SubTask subTask = tm.getSubTaskByID(tm.createSubTask(new SubTask("b", "a", Status.NEW, 1, tm.getEpicTaskByID(epicId) ,Duration.ofNanos(1), LocalDateTime.of(2019, 03, 28, 14, 33, 48, 640000))));
        subTask.setStatus(Status.IN_PROGRESS);
        tm.updateSubTask(subTask);
        ArrayList<String> bf = readerFile();

        assertEquals(3, readerFile().size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,b,IN_PROGRESS,a," + epicId + ",2019-03-28T14:33:48.000640,PT0.000000001S", bf.getLast(), "SubTask не был обновлен");
    }
}