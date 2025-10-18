import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import utils.Managers;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void createTaskManager() {
        tm = (InMemoryTaskManager) Managers.getDefault();
    }
}