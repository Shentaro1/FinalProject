import org.junit.jupiter.api.Test;
import tasks.EpicTask;
import tasks.Task;
import static org.junit.jupiter.api.Assertions.*;

class AbstractTaskTest {
    @Test
    void testDefaultIdIs0() {
        Task task1 = new Task("a", "b");
        assertEquals(0, task1.getId(), "Базовые id не совпадают");
    }

    @Test
    void testEqualsOfTheSameClass() {
        Task task1 = new Task("a", "b");
        Task task2 = new Task("c", "d");
        assertTrue(task1.equals(task2), "Экземпляры одного класса не равны, когда равны их id");
    }

    @Test
    void testEqualsOfTheDifferentClass() {
        Task task = new Task("a", "b");
        EpicTask epicTask = new EpicTask("c", "d");
        assertTrue(task.equals(epicTask), "Экземпляры разных классов не равны, когда равны их id");
    }

    @Test
    void testHashCode() {
        Task task1 = new Task("a", "b");
        Task task2 = new Task("c", "d");
        assertTrue(
                task1.hashCode() == task2.hashCode(),
                "hashCode не совпадает, когда равны их id"
        );
    }
}