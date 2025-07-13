package managers;

import tasks.AbstractTask;
import java.util.ArrayList;

public interface HistoryManager {
    void add(AbstractTask task);
    ArrayList<AbstractTask> getHistory();
    void remove(int id);
}
