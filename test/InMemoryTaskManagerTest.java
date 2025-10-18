import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.*;
import utils.Managers;

import java.util.ArrayList;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void createTaskManager() {
        tm = (InMemoryTaskManager) Managers.getDefault();
    }
}