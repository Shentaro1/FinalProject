import exception.NotFoundException;
import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import utils.Managers;
import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
        tm = (FileBackedTaskManager) Managers.getFileBackedManager(file);
    }

    @Test
    void testCreateTask() {
        super.testCreateTask();
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,TASK,b,NEW,a,,,", bf.getLast(), "");
    }

    @Test
    void testCreateEpicTask() {
        super.testCreateEpicTask();
        ArrayList<String> bf = readerFile();

        assertEquals(2, bf.size(), "Размер не соответствует");
        assertEquals("0,EPIC,b,NEW,a,,,", bf.getLast(), "");
    }

    @Test
    void testCreateSubTask() throws NotFoundException {
        super.testCreateSubTask();
        ArrayList<String> bf = readerFile();

        assertEquals(3, bf.size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,d,NEW,c,0,,", bf.getLast(), "");
    }

    @Test
    void testCreateHeading() {
        tm.createTask(new Task("a", "b"));
        assertEquals(
                "id,type,name,status,description,epic,startTime,duration",
                readerFile().getFirst(),
                "Шапка неправильная"
        );
    }

    @Test
    void testClearTask() {
        super.testClearTask();
        assertEquals(1, readerFile().size(), "Tasks не были удалены");
    }

    @Test
    void testClearEpicTask() {
        super.testClearEpicTask();
        assertEquals(1, readerFile().size(), "EpicTasks не были удалены");
    }

    @Test
    void testClearSubTask() throws NotFoundException {
        super.testClearSubTask();

        assertEquals(2, readerFile().size(), "SubTasks не были удалены");
    }

    @Test
    void testUpdateTask() throws NotFoundException {
        super.testUpdateTask();
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,TASK,c,NEW,a,,,", bf.getLast(), "Task не был обновлен");
    }

    @Test
    void testUpdateEpicTask() throws NotFoundException {
        super.testUpdateEpicTask();
        ArrayList<String> bf = readerFile();

        assertEquals(2, readerFile().size(), "Размер не соответствует");
        assertEquals("0,EPIC,c,NEW,a,,,", bf.getLast(), "EpicTask не был обновлен");
    }

    @Test
    void testUpdateSubTask() throws NotFoundException {
        super.testUpdateSubTask();
        ArrayList<String> bf = readerFile();

        assertEquals(3, readerFile().size(), "Размер не соответствует");
        assertEquals("1,SUBTASK,c,NEW,a,0,,", bf.getLast(), "SubTask не был обновлен");
    }
}