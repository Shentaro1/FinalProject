package managers;

import tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counterID;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HistoryManager historyManager;
    private final TreeMap<LocalDateTime, AbstractTask> allTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        allTasks = new TreeMap<>();
        this.historyManager = historyManager;
    }

    //Конструктор для создания FileBackedTaskManager
    protected InMemoryTaskManager(InMemoryTaskManager taskManager) {
        counterID = taskManager.counterID;
        tasks = taskManager.tasks;
        subTasks = taskManager.subTasks;
        epicTasks = taskManager.epicTasks;
        allTasks = taskManager.allTasks;
        historyManager = taskManager.historyManager;
    }

    public void getPrioritizedTasks() {
        allTasks.values()
                .forEach(System.out::println);
    }

    //a. Получение списка всех задач.
    public ArrayList<Task> getAllTask() {
        return tasks.values().stream()
                .map(Task::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<SubTask> getAllSubTask() {
        return subTasks.values().stream()
                .map(SubTask::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<EpicTask> getAllEpicTask() {
        return epicTasks.values().stream()
                .map(EpicTask::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //b. Удаление всех задач.
    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubTasks() {
        subTasks.clear();
        epicTasks.values()
                .forEach(epicTask -> {
                    epicTask.getSubTasks().clear();
                    epicTask.updateStatus();
                });
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
        if (!isTaskOverlap(task)) {
            allTasks.put(task.getStartTime(), task);
        }
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
        if (!isTaskOverlap(subTask)) {
            allTasks.put(subTask.getStartTime(), subTask);
        }
        return subTask.getId();
    }

    public int createEpicTask(EpicTask epicTask) {
        if (epicTask == null) {
            return -1;
        }
        EpicTask newEpic = new EpicTask(epicTask);
        newEpic.setId(counterID++);
        epicTasks.put(newEpic.getId(), newEpic);
        return newEpic.getId();
    }

    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public boolean updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId()))
            return false;
        Task currentTask = tasks.get(task.getId());
        currentTask.setName(task.getName());
        currentTask.setDescription(task.getDescription());
        currentTask.setStatus(task.getStatus());
        currentTask.setDuration(task.getDuration());
        currentTask.setStartTime(task.getStartTime());
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
        currentSubTask.setDuration(subTask.getDuration());
        currentSubTask.setStartTime(subTask.getStartTime());
        currentSubTask.getFk_epicTask().updateStatus();
        return true;
    }

    public boolean updateEpicTask(EpicTask epicTask) {
        if (epicTask == null || !epicTasks.containsKey(epicTask.getId()))
            return false;

        EpicTask currentEpicTask = epicTasks.get(epicTask.getId());
        currentEpicTask.setName(epicTask.getName());
        currentEpicTask.setDescription(epicTask.getDescription());
        currentEpicTask.setDuration(epicTask.getDuration());
        currentEpicTask.setStartTime(epicTask.getStartTime());
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
        epicTask.getSubTasks().stream()
                .map(SubTask::getId)
                .forEach(subTasks::remove);
        return true;
    }

    //g. Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getAllSubTaskByEpicTask(int id) {
        EpicTask epicTask = epicTasks.get(id);
        if (epicTask == null)
            return null;
        return epicTask.getSubTasks().stream()
                .map(SubTask::new)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    //получение истории
    public ArrayList<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    public void setCounterID(int id) {
        this.counterID = id;
    }

    //метод для проверки на пересечение задач
    public boolean isTaskOverlap(AbstractTask task) {
        LocalDateTime newStart = task.getStartTime();
        LocalDateTime newEnd = task.getEndTime();
        if (allTasks.isEmpty()) {
            return false;
        }

        if (newStart == null || newEnd == null) {
            return true;
        }
        for (Map.Entry<LocalDateTime, AbstractTask> entry : allTasks.entrySet()) {
            LocalDateTime oldStart = entry.getKey();
            LocalDateTime oldEnd = entry.getValue().getEndTime();
            if (!(oldEnd.isBefore(oldStart) || oldStart.isAfter(newEnd))) {
                return false;
            }
        }
        return true;
    }
}
