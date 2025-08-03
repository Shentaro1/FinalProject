package utils;

import managers.*;
import tasks.AbstractTask;

import java.io.File;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager<AbstractTask>();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefaultFileBack(File file) {
        return new FileBackedTaskManager(new InMemoryTaskManager(getDefaultHistory()), file);
    }



}

