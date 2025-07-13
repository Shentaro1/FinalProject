package managers;

import tasks.*;
import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTask();
    ArrayList<SubTask> getAllSubTask();
    ArrayList<EpicTask> getAllEpicTask();

    void clearTasks();
    void clearSubTasks();
    void clearEpicTasks();

    Task getTaskByID(int id);
    SubTask getSubTaskByID(int id);
    EpicTask getEpicTaskByID(int id);

    int createTask(Task task);
    int createSubTask(SubTask subTask);
    int createEpicTask(EpicTask epicTask);

    boolean updateTask(Task task);
    boolean updateSubTask(SubTask subTask);
    boolean updateEpicTask(EpicTask epicTask);

    boolean deleteTaskByID(int id);
    boolean deleteSubTaskByID(int id);
    boolean deleteEpicTaskByID(int id);

    ArrayList<SubTask> getAllSubTaskByEpicTask(int id);
    ArrayList<AbstractTask> getHistory();
}
