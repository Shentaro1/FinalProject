import managers.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefaultHistory() {
        HistoryManager hm = Managers.getDefaultHistory();
        assertNotNull(hm,"Выдается null");
        assertEquals(
                InMemoryHistoryManager.class,
                hm.getClass(),
                "Возвращающийся HistoryManager не совпадает с базовым"
        );
    }

    @Test
    void testGetDefault() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm, "Выдается null");
        assertEquals(
                InMemoryTaskManager.class,
                tm.getClass(),
                "Возвращающийся TaskManager не совпадает с базовым"
        );
    }
}
