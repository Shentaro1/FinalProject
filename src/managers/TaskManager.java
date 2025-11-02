package managers;
import exception.NotFoundException;
import tasks.*;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    List<Task> getAllTask();
    List<SubTask> getAllSubTask();
    List<EpicTask> getAllEpicTask();

    void clearTasks();
    void clearSubTasks();
    void clearEpicTasks();

    Task getTaskByID(int id) throws NotFoundException;
    SubTask getSubTaskByID(int id) throws NotFoundException;
    EpicTask getEpicTaskByID(int id) throws NotFoundException;

    int createTask(Task task);
    int createSubTask(SubTask subTask) throws NotFoundException;
    int createEpicTask(EpicTask epicTask);

    int updateTask(Task task) throws NotFoundException;
    int updateSubTask(SubTask subTask) throws NotFoundException;
    int updateEpicTask(EpicTask epicTask) throws NotFoundException;

    boolean deleteTaskByID(int id);
    boolean deleteSubTaskByID(int id);
    boolean deleteEpicTaskByID(int id);

    List<SubTask> getAllSubTaskByEpicTask(int id) throws NotFoundException;
    ArrayList<AbstractTask<?>> getHistory();
    List<AbstractTask<?>> getPrioritizedTasks();
}