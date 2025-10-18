import managers.FileBackedTaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import types.Status;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    ArrayList<String> readerFile() {
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

    @Test
    void testCreateTask() {
        fb.createTask(new Task("a", "b"));
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,TASK,b,NEW,a,", bf.getLast(), "");
    }

    @Test
    void testCreateEpicTask() {
        fb.createEpicTask(new EpicTask("a", "b"));
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,EPICTASK,b,NEW,a,", bf.getLast(), "");
    }

    @Override
    @Test
    void testCreateSubTask() {
        int epicId = fb.createEpicTask(new EpicTask("a", "b"));
        fb.createSubTask(new SubTask("c", "d", fb.getEpicTaskByID(epicId)));
        ArrayList<String> bf = readerFile();

        assertEquals(3, bf.size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,d,NEW,c," +  epicId, bf.getLast(), "");
    }

    @Test
    void testCreateHeading() {
        fb.createTask(new Task("a", "b"));
        assertEquals(
                "id,type,name,status,description,epic",
                readerFile().getFirst(),
                "Шапка неправильная"
        );
    }

    @Override
    @Test
    void testClearTask() {
        fb.createTask(new Task("a", "b"));
        fb.clearTasks();
        assertEquals(1, readerFile().size(), "Tasks не были удалены");
    }

    @Test
    void testClearEpicTask() {
        fb.createEpicTask(new EpicTask("a", "b"));
        fb.clearEpicTasks();
        assertEquals(1, readerFile().size(), "EpicTasks не были удалены");
    }

    @Test
    void testClearSubTask() {
        int epicId = fb.createEpicTask(new EpicTask("a", "b"));
        fb.createSubTask(new SubTask("c", "d", fb.getEpicTaskByID(epicId)));
        fb.clearEpicTasks();
        fb.clearSubTasks();

        assertEquals(1, readerFile().size(), "SubTasks не были удалены");
    }

    @Test
    void testUpdateTask() {
        Task task = fb.getTaskByID(fb.createTask(new Task("a", "b")));
        task.setStatus(Status.DONE);
        fb.updateTask(task);
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,TASK,b,DONE,a,", bf.getLast(), "Task не был обновлен");
    }

    @Test
    void testUpdateEpicTask() {
        EpicTask epicTask = fb.getEpicTaskByID(fb.createEpicTask(new EpicTask("a", "b")));
        epicTask.setDescription("c");
        fb.updateEpicTask(epicTask);
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,EPICTASK,b,NEW,c,", bf.getLast(), "EpicTask не был обновлен");
    }

    @Test
    void testUpdateSubTask() {
        int epicId = fb.createEpicTask(new EpicTask("a", "b"));
        SubTask subTask = fb.getSubTaskByID(fb.createSubTask(new SubTask("c", "d", fb.getEpicTaskByID(epicId))));
        subTask.setStatus(Status.IN_PROGRESS);
        fb.updateSubTask(subTask);
        ArrayList<String> bf = readerFile();

        assertEquals(3, readerFile().size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,d,IN_PROGRESS,c," + epicId, bf.getLast(), "SubTask не был обновлен");
    }
}