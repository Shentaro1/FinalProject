package managers;

import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int counterID;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        this.historyManager = historyManager;
    }

    //a. Получение списка всех задач.
    public ArrayList<Task> getAllTask() {
        ArrayList<Task> resultTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            resultTasks.add(new Task(task));
        }
        return resultTasks;
    }

    public ArrayList<SubTask> getAllSubTask() {
        ArrayList<SubTask> result_subTasks = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            result_subTasks.add(new SubTask(subTask));
        }
        return result_subTasks;
    }

    public ArrayList<EpicTask> getAllEpicTask() {
        ArrayList<EpicTask> result_epicTasks = new ArrayList<>();
        for (EpicTask epicTask : epicTasks.values()) {
            result_epicTasks.add(new EpicTask(epicTask));
        }
        return result_epicTasks;
    }

    //b. Удаление всех задач.
    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        for (EpicTask epicTask : epicTasks.values()) {
            epicTask.getSubTasks().clear();
            epicTask.updateStatus();
        }
    }

    public void clearEpicTasks() {
        epicTasks.clear();
        subTasks.clear();
    }

    //c. Получение по идентификатору.
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task == null)
            return null;
        historyManager.add(task);
        return new Task(task);
    }

    public SubTask getSubTaskByID(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask == null)
            return null;
        historyManager.add(subTask);
        return new SubTask(subTask);
    }

    public EpicTask getEpicTaskByID(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask == null)
            return null;
        historyManager.add(epicTask);
        return new EpicTask(epicTask);
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public int createTask(Task task) {
        if (task == null)
            return -1;
        task = new Task(task);
        task.setId(counterID++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createSubTask(SubTask subTask) {
        if (subTask == null || !epicTasks.containsValue(subTask.getFk_epicTask()))
            return -1;
        EpicTask epicTask = epicTasks.get(subTask.getFk_epicTask().getId());
        subTask = new SubTask(subTask);
        subTask.setId(counterID++);
        subTask.setFk_epicTask(epicTask);
        epicTask.getSubTasks().add(subTask);
        epicTask.updateStatus();
        subTasks.put(subTask.getId(), subTask);
        return subTask.getId();
    }

    public int createEpicTask(EpicTask epicTask) {
        if (epicTask == null)
            return -1;
        epicTask = new EpicTask(epicTask);
        epicTask.setId(counterID++);
        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask.getId();
    }


    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId()))
            return false;
        Task currentTask = tasks.get(task.getId());
        currentTask.setName(task.getName());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());
        return true;
    }

    public boolean updateSubTask(SubTask subTask) {
        if (
                subTask == null ||
                        !subTasks.containsKey(subTask.getId()) ||
                        !epicTasks.containsValue(subTask.getFk_epicTask())
        )
            return false;

        SubTask currentSubTask = subTasks.get(subTask.getId());
        if (!currentSubTask.getFk_epicTask().equals(subTask.getFk_epicTask())) {
            currentSubTask.getFk_epicTask().getSubTasks().remove(currentSubTask);
            currentSubTask.setFk_epicTask(epicTasks.get(subTask.getFk_epicTask().getId()));
            currentSubTask.getFk_epicTask().getSubTasks().add(currentSubTask);
        }

        currentSubTask.setName(subTask.getName());
        currentSubTask.setDescription(subTask.getDescription());
        currentSubTask.setStatus(subTask.getStatus());
        currentSubTask.getFk_epicTask().updateStatus();
        return true;
    }

    public boolean updateEpicTask(EpicTask epicTask) {
        if (epicTask == null || !epicTasks.containsKey(epicTask.getId()))
            return false;

        EpicTask currentEpicTask = epicTasks.get(epicTask.getId());
        currentEpicTask.setName(epicTask.getName());
        currentEpicTask.setDescription(epicTask.getDescription());
        return true;
    }


    //f. Удаление по идентификатору.
    public boolean deleteTaskByID(int id) {
        historyManager.remove(id);
        return tasks.remove(id) != null;
    }

    public boolean deleteSubTaskByID(int id) {
        historyManager.remove(id);
        SubTask subTask = subTasks.remove(id);
        if (subTask == null)
            return false;
        subTask.getFk_epicTask().getSubTasks().remove(subTask);
        return true;
    }

    public boolean deleteEpicTaskByID(int id) {
        historyManager.remove(id);
        EpicTask epicTask = epicTasks.remove(id);
        if (epicTask == null)
            return false;
        for (SubTask subTask : epicTask.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        return true;
    }

    //g. Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getAllSubTaskByEpicTask(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask == null)
            return null;
        ArrayList<SubTask> resultSubTasks = new ArrayList<>();
        for (SubTask subTask : epicTask.getSubTasks()) {
            resultSubTasks.add(new SubTask(subTask));
        }
        return resultSubTasks;
    }

    //получение истории
    public ArrayList<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }
}
