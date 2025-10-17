import managers.FileBackedTaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Managers;

import javax.imageio.IIOException;
import java.io.File;
import java.io.IOException;

abstract class TaskManagerTest <T extends TaskManager> {
    T tm;

    @BeforeEach
    abstract void createInMemoryTaskManager();

    @Test
    abstract void testCreateTask();

    @Test
    abstract void testCreateEpicTask();

    @Test
    abstract void testCreateSubTask();

    @Test
    abstract void testClearTasks();

    @Test
    abstract void testClearSubTask();

    @Test
    abstract void testClearEpicTask();

    @Test
    abstract void testUpdateTask();

    @Test
    abstract void testUpdateSubTask();

    @Test
    abstract void testUpdateEpicTask();



}
