import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import utils.Managers;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager hm;

    @BeforeEach
    void createInMemoryHistoryManager() {
        hm = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    @Test
    void testGetHistory() {
        assertNotNull(hm.getTasks(), "getHistory ничего не возвращает");
    }

    @Test
    void testAddTask() {
        hm.add(new Task("a", "b"));

        assertFalse(hm.getTasks().isEmpty(), "Список с историей пуст");
    }

    @Test
    void testRemoveTask() {
        hm.add(new Task("a", "b"));

        hm.remove(0);

        assertTrue(hm.getTasks().isEmpty(), "Задача не была удалена");
    }

    @Test
    void testOverWriting() {
        hm.add(new Task("a", "b"));
        hm.add(new Task("new", "b"));

        assertEquals(1, hm.getTasks().size(), "Задача была добавлена");
        assertEquals("new", hm.getTasks().getFirst().getDescription(), "Поле name не поменялось");
    }
}