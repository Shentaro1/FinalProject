package managers;
import exception.NotFoundException;
import tasks.*;
import types.Status;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private int counterID;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, SubTask> subTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HistoryManager historyManager;
    private final TreeSet<AbstractTask<?>> prioritizedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>();
        subTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        this.historyManager = historyManager;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(AbstractTask::getStartTime));
    }

    protected InMemoryTaskManager(InMemoryTaskManager donor) {
        tasks = donor.tasks;
        subTasks = donor.subTasks;
        epicTasks = donor.epicTasks;
        historyManager = donor.historyManager;
        counterID = donor.counterID;
        prioritizedTasks = donor.prioritizedTasks;
    }

    protected void setCounterID(int counterID) {
        this.counterID = counterID;
    }

    //a. Получение списка всех задач.
    public List<Task> getAllTask() {
        return getListTasksOnMap(tasks);
    }

    public List<SubTask> getAllSubTask() {
        return getListTasksOnMap(subTasks);
    }

    public List<EpicTask> getAllEpicTask() {
        return getListTasksOnMap(epicTasks);
    }

    private <T extends AbstractTask<T>> List<T> getListTasksOnMap(HashMap<Integer, T> list) {
        return list.values()
                .stream()
                .map(AbstractTask::copy)
                .collect(Collectors.toList());

    }

    //b. Удаление всех задач.
    public void clearTasks() {
        //target: удаление tasks из истории и HashMap
        for (Map.Entry<Integer, Task> entry: tasks.entrySet()) {
            historyManager.remove(entry.getKey());

            if (entry.getValue().getEndTime() != null)
                prioritizedTasks.remove(entry.getValue());

            tasks.remove(entry.getKey());
        }
    }

    public void clearSubTasks() {
        //target: удаление subTasks из истории и HashMap
        for (Map.Entry<Integer, SubTask> entry: subTasks.entrySet()) {
            historyManager.remove(entry.getKey());

            if (entry.getValue().getEndTime() != null)
                prioritizedTasks.remove(entry.getValue());

            subTasks.remove(entry.getKey());
        }

        //target: очистка epicTask от удаленных subTasks
        epicTasks.values().forEach(EpicTask::clearSubTasks);
    }

    public void clearEpicTasks() {
        //target: удаление epicTask из истории и HashMap
        for (Integer key : epicTasks.keySet()) {
            historyManager.remove(key);
            epicTasks.remove(key);
        }

        //target: удаление subTask из истории и HashMap
        for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
            historyManager.remove(entry.getKey());

            if (entry.getValue().getEndTime() != null)
                prioritizedTasks.remove(entry.getValue());

            subTasks.remove(entry.getKey());
        }
    }

    //c. Получение по идентификатору.
    public Task getTaskByID(int id) throws NotFoundException {
        return getTaskByMap(tasks, id);
    }

    public SubTask getSubTaskByID(int id) throws NotFoundException {
        return getTaskByMap(subTasks, id);
    }

    public EpicTask getEpicTaskByID(int id) throws NotFoundException {
        return getTaskByMap(epicTasks, id);
    }

    private <T extends AbstractTask<T>> T getTaskByMap(HashMap<Integer, T> list, int id) throws NotFoundException {
        T task = list.get(id);

        //check: задача может быть null
        if (task == null)
            throw new NotFoundException("Задача c id=" + id + " не была найдена");

        //target: создание просмотра задачи
        historyManager.add(task.copy());

        return task.copy();
    }

    //d. Создание. Сам объект должен передаваться в качестве параметра.
    public int createTask(Task task) {
        //check: задача может быть null
        if (task == null)
            return -1;

        //target: создание копии для изоляции
        task = task.copy();

        if (task.getEndTime() != null) {
            if (!checkingTheNoIntersectionWithEveryone(task))
                return -3;
            prioritizedTasks.add(task);
        }

        //target: установка id
        task.setId(counterID++);
        task.setStatus(Status.NEW);

        tasks.put(task.getId(), task);
        return task.getId();
    }

    public int createSubTask(SubTask subTask) throws NotFoundException {
        //check: задача может быть null
        if (subTask == null)
            return -1;

        //check: id на EpicTask может не существовать
        if (!epicTasks.containsKey(subTask.getFkEpicTask()))
            throw new NotFoundException("Subtask c id=" + subTask.getId() + " не может быть cоздан, так как ee epicTask не существует");


        //target: создание копии для изоляции
        subTask = subTask.copy();

        if (subTask.getEndTime() != null) {
            if (!checkingTheNoIntersectionWithEveryone(subTask))
                return -3;
            prioritizedTasks.add(subTask);
        }

        //target: получение epicTask, на который ссылается subTask
        EpicTask epicTask = epicTasks.get(subTask.getFkEpicTask());

        //target: установка id
        subTask.setId(counterID++);
        subTask.setStatus(Status.NEW);


        //target: добавление subTask в epicTask
        epicTask.addSubTasks(subTask.getId());

        subTasks.put(subTask.getId(), subTask);

        //target: обновление epicTask, так как мы добавили новый элемент
        updateAttributesFromEpicTask(epicTask);

        return subTask.getId();
    }


    public int createEpicTask(EpicTask epicTask) {
        //check: задача может быть null
        if (epicTask == null)
            return -1;

        //target: создание копии для изоляции
        epicTask = epicTask.copy();

        //target: установка id
        epicTask.setId(counterID++);
        epicTask.setStatus(Status.NEW);

        //target: в subTasks может оказаться мусор
        epicTask.clearSubTasks();

        epicTasks.put(epicTask.getId(), epicTask);
        return epicTask.getId();
    }


    //e. Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public int updateTask(Task task) throws NotFoundException {
        //check: задача может быть null
        if (task == null)
            return -1;

        //check: задача может не существовать
        if (!tasks.containsKey(task.getId()))
            throw new NotFoundException("Task c id=" + task.getId() + " не может быть обновлена, так как ее не существует");

        //target: создание копии для изоляции
        task = task.copy();

        Task oldTask = tasks.get(task.getId());
        if (oldTask != null && oldTask.getEndTime() != null)
            prioritizedTasks.remove(oldTask);

        if (task.getEndTime() != null) {
            if (!checkingTheNoIntersectionWithEveryone(task))
                return -3;
            prioritizedTasks.add(task);
        }


        tasks.put(task.getId(), task);
        return 0;
    }

    public int updateSubTask(SubTask subTask) throws NotFoundException {
        //check: задача может быть null
        if (subTask == null)
            return -1;

        //check: задача может не существовать
        if (!subTasks.containsKey(subTask.getId()))
            throw new NotFoundException("Subtask c id=" + subTask.getId() + " не может быть обновлена, так как ее не существует");

        //check: epicTask, на который указывает subTask, может не существовать
        if (!epicTasks.containsKey(subTask.getFkEpicTask()))
            throw new NotFoundException("Subtask c id=" + subTask.getId() + " не может быть обновлена, так как ee epicTask не существует");

        //target: получение текущего и нового subTask
        SubTask currentSubTask = subTasks.get(subTask.getId());

        //target: получение epicTask, на который ссылается subTask
        EpicTask epicTask = epicTasks.get(subTask.getFkEpicTask());

        if (currentSubTask.getFkEpicTask() != subTask.getFkEpicTask()) {
            //target: получение текущего epicTask, на который ссылался subTask
            EpicTask prevEpicTask = epicTasks.get(currentSubTask.getFkEpicTask());

            //target: удаляем subTask из текущего epicTask
            prevEpicTask.deleteSubTasks(currentSubTask.getId());

            //target: добавляем subTask в новый epicTask
            epicTask.addSubTasks(currentSubTask.getId());

            //target: обновление статуса у текущего epicTask
            updateAttributesFromEpicTask(prevEpicTask);
        }

        //target: создание копии для изоляции
        subTask = subTask.copy();

        SubTask oldTask = subTasks.get(subTask.getId());
        if (oldTask != null && oldTask.getEndTime() != null)
            prioritizedTasks.remove(oldTask);

        if (subTask.getEndTime() != null) {
            if (!checkingTheNoIntersectionWithEveryone(subTask))
                return -3;
            prioritizedTasks.add(subTask);
        }

        subTasks.put(subTask.getId(), subTask);

        //target: обновление статуса у epicTask, так как мы изменили элемент
        updateAttributesFromEpicTask(epicTask);
        return 0;
    }

    public int updateEpicTask(EpicTask epicTask) throws NotFoundException {
        //check: задача может быть null
        if (epicTask == null)
            return -1;

        //check: задача может не существовать
        if (!epicTasks.containsKey(epicTask.getId()))
            throw new NotFoundException("EpicTask c id=" + epicTask.getId() + " не может быть обновлена, так как ее не существует");

        //target: получение текущего epicTask
        EpicTask currentEpicTask = epicTasks.get(epicTask.getId());

        //target: создание копии для изоляции
        epicTask = epicTask.copy();

        //check: список subTask должен остаться прежним
        if (!currentEpicTask.getSubTasks().equals(epicTask.getSubTasks()))
            return -3;

        //target: статус должен остаться прежним
        epicTask.setStatus(currentEpicTask.getStatus());

        epicTasks.put(epicTask.getId(), epicTask);
        return 0;
    }


    //f. Удаление по идентификатору.
    public boolean deleteTaskByID(int id) {
        Task task;

        if ((task = tasks.remove(id)) == null) {
            return false;
        }

        if (task.getEndTime() != null)
            prioritizedTasks.remove(task);

        //target: удаление из истории, если задача там есть
        historyManager.remove(id);

        return true;
    }

    public boolean deleteSubTaskByID(int id) {
        //target: получаем удаляемый subTask и его удаляем
        SubTask subTask = subTasks.remove(id);

        //check: задача может не существовать
        if (subTask == null)
            return false;

        //target: удаление из истории, если задача там есть
        historyManager.remove(id);

        //target: получаем связанный epicTask
        EpicTask epicTask = epicTasks.get(subTask.getFkEpicTask());

        //target: удаляем subTask из epicTask
        epicTask.deleteSubTasks(subTask.getId());

        if (subTask.getEndTime() != null)
            prioritizedTasks.remove(subTask);

        return true;
    }

    public boolean deleteEpicTaskByID(int id) {
        //target: получаем удаляемый epicTask
        EpicTask epicTask = epicTasks.get(id);

        //check: задача может не существовать
        if (epicTask == null)
            return false;

        //target: удаление из истории, если задача там есть
        historyManager.remove(id);

        //target: удаление связанных subTask
        for (int idSubTask : epicTask.getSubTasks()) {
            deleteSubTaskByID(idSubTask);
        }

        epicTasks.remove(id);
        return true;
    }

    //g. Получение списка всех подзадач определённого эпика.
    public List<SubTask> getAllSubTaskByEpicTask(int id) throws NotFoundException {
        //target: получаем epicTask
        EpicTask epicTask = epicTasks.get(id);

        //check: задача может не существовать
        if (epicTask == null)
            throw new NotFoundException("EpicTask c id=" + id + " не существует");

        return epicTask.getSubTasks()
                .stream()
                .map(subTasks::get)
                .collect(Collectors.toList());
    }

    //h. Получение истории
    public ArrayList<AbstractTask<?>> getHistory() {
        return historyManager.getTasks();
    }

    public List<AbstractTask<?>> getPrioritizedTasks() {
        return prioritizedTasks
                .stream()
                .map(AbstractTask::copy)
                .collect(Collectors.toList());
    }

    private void updateAttributesFromEpicTask(EpicTask epicTask) throws NotFoundException {
        boolean[] pState = new boolean[3];
        epicTask.setDuration(Duration.ZERO);

        getAllSubTaskByEpicTask(epicTask.getId())
                .stream()
                .peek((subTask) -> {
                    switch (subTask.getStatus()) {
                        case IN_PROGRESS -> pState[0] = true;
                        case NEW -> pState[1] = true;
                        case DONE -> pState[2] = true;
                    }
                })
                .filter((subTask) -> subTask.getEndTime() != null)
                .peek((subTask) ->
                        epicTask.setDuration(
                                epicTask.getDuration().plus(
                                        subTask.getDuration()
                                )
                        )
                )
                .min(Comparator.comparing(AbstractTask::getStartTime))
                .ifPresent(
                        subTask -> epicTask.setStartTime(subTask.getStartTime())
                );

        if (epicTask.getDuration().equals(Duration.ZERO))
            epicTask.setDuration(null);

        epicTask.setStatus(
                pState[0] || (pState[1] && pState[2]) ? Status.IN_PROGRESS :
                        pState[2] ? Status.DONE : Status.NEW
        );
    }

    private boolean checkingTheNoIntersection(AbstractTask<?> task1, AbstractTask<?> task2) {
        if (task1 == null || task2 == null)
            return true;

        return task1.getStartTime().isAfter(task2.getEndTime()) ||
                task1.getEndTime().isBefore(task2.getStartTime());
    }

    private boolean checkingTheNoIntersectionWithEveryone(AbstractTask<?> task) {
        AbstractTask<?> lower = prioritizedTasks.floor(task);
        AbstractTask<?> higher = prioritizedTasks.ceiling(task);

        return checkingTheNoIntersection(task, lower) && checkingTheNoIntersection(task, higher);
    }
}