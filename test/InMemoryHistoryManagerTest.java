import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Task;
import utils.Managers;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager<AbstractTask> hm;

    @BeforeEach
    void createInMemoryHistoryManager() {
        hm = (InMemoryHistoryManager<AbstractTask>) Managers.getDefaultHistory();
    }

    @Test
    void testAdd() {
        Task task = new Task("a", "b");
        hm.add(task);
        ArrayList<AbstractTask> list = hm.getHistory();

        assertEquals(1, list.size(), "Неверное кол-во задач");
        assertEquals(task, list.getLast(), "Задачи не совпадают");
    }

    @Test
    void testGetHistory() {
        assertNotNull(hm.getHistory(), "getHistory ничего не возвращает");
    }

    @Test
    void removeAndAddToEnd() {
        hm.add(new Task("a", "b"));
        hm.add(new Task("new", "b"));

        assertEquals(1, hm.getHistory().size(), "Размер не совпадает");
        assertEquals("new", hm.getHistory().getLast().getDescription());
    }

}