package tasks;
import types.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends AbstractTask<EpicTask> {
    private final ArrayList<Integer> subTasks;

    public EpicTask(EpicTask epicTask) {
        super(epicTask);
        subTasks = epicTask.getSubTasks();
    }

    public EpicTask(String description, String name) {
        super(description, name);
        subTasks = new ArrayList<>();
    }

    public EpicTask(int id, String description, String name, Status status) {
        super(id, description, name, status);
        subTasks = new ArrayList<>();
    }

    public EpicTask(String description, String name, LocalDateTime startTime, Duration duration) {
        super(description, name, startTime, duration);
        subTasks = new ArrayList<>();
    }

    public EpicTask(int id, String description, String name, Status status, LocalDateTime startTime, Duration duration) {
        super(id, description, name, status, startTime, duration);
        subTasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public int addSubTasks(int id) {
        //error: попытка добавить уже существующий id
        if (subTasks.contains(id))
            return -1;

        subTasks.add(id);
        return 0;
    }

    public int deleteSubTasks(int id) {
        //error: не удалось найти элемент
        if (!subTasks.remove(Integer.valueOf(id)))
            return -1;

        return 0;
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks.size=" + subTasks.size() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                "} ";
    }

    @Override
    public EpicTask copy() {
        return new EpicTask(this);
    }
}