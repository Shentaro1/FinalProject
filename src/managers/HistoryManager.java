package managers;
import tasks.AbstractTask;
import java.util.ArrayList;

public interface HistoryManager {
    void add(AbstractTask<?> task);
    void remove(int id);
    ArrayList<AbstractTask<?>> getTasks();
}