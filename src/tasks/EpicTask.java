package tasks;
import types.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class EpicTask extends AbstractTask {
    private final ArrayList<SubTask> subTasks;
    LocalDateTime endTime;

    //Конструктор копирования
    public EpicTask(EpicTask epicTask) {
        super(epicTask);
        subTasks = new ArrayList<>(epicTask.getSubTasks());
        endTime = epicTask.endTime;
    }

    //Дефолтный конструктор
    public EpicTask(String description, String name) {
        super(description, name);
        subTasks = new ArrayList<>();
        endTime = null;
    }

    //Конструктор для создания при помощи файла
    public EpicTask(String name, String description, Status status, int id, Duration duration, LocalDateTime startTime) {
        super(id, duration, startTime, description, name, status);
        this.subTasks = new ArrayList<SubTask>();
    }

    @Override
    public LocalDateTime getStartTime() {
        if (subTasks == null || subTasks.isEmpty()) {
            return null;
        }

        LocalDateTime flag = null;
        for (SubTask subTask : subTasks) {
            LocalDateTime startTime = subTask.getStartTime();
            if (startTime != null) {
                if (flag == null || startTime.isBefore(flag)) {
                    flag = startTime;
                }
            }
        }
        return flag;
    }

    @Override
    public Duration getDuration() {
        LocalDateTime start = getStartTime();
        LocalDateTime end = getEndTime();

        if (start == null || end == null) {
            return null;
        }

        return Duration.between(start, end);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subTasks == null || subTasks.isEmpty()) {
            return null;
        }

        LocalDateTime flag = null;
        for (SubTask subTask : subTasks) {
            LocalDateTime startTime = subTask.getStartTime();
            Duration duration = subTask.getDuration();

            if (startTime != null && duration != null) {
                LocalDateTime endTime = startTime.plus(duration);
                if (flag == null || endTime.isAfter(flag)) {
                    flag = endTime;
                }
            }
        }
        return flag;
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean isExistStatusNEW = false;
        boolean isExistStatusDONE = false;
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case IN_PROGRESS:
                    status = Status.IN_PROGRESS;
                    return;
                case NEW:
                    if (isExistStatusDONE) {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    isExistStatusNEW = true;
                    break;
                case DONE:
                    if (isExistStatusNEW) {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    isExistStatusDONE = true;
                    break;
            }
        }
        if (isExistStatusDONE)
            status = Status.DONE;
        else
            status = Status.NEW;
    }

    //get
    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks.size=" + subTasks.size() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", duration='" + getDuration() + "'" +
                ", startTime=" + getStartTime() +
                "} ";
    }

    @Override
    public EpicTask copy() {
        return new EpicTask(this);
    }
}