import managers.*;
import tasks.AbstractTask;

public class Managers {
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager<AbstractTask>();
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

}

