package managers;

import tasks.Task;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final String enteredPath;

    public FileBackedTaskManager(String enteredPath, HistoryManager historyManager) {
        super(historyManager);
        this.enteredPath = enteredPath;
    }


    public void save() {


    }

}
