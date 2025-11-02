package tasks;

import types.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends AbstractTask<SubTask> {
    private int fk_epicTask;

    public SubTask(String description, String name, int fk_epicTask, LocalDateTime startTime, Duration duration) {
        super(description, name, startTime, duration);
        this.fk_epicTask = fk_epicTask;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        fk_epicTask = subTask.getFk_epicTask();
    }

    public SubTask(String description, String name, int fk_epicTask) {
        super(description, name);
        this.fk_epicTask = fk_epicTask;
    }

    public SubTask(int id, String description, String name, Status status, int fk_epicTask, LocalDateTime startTime, Duration duration) {
        super(id, description, name, status, startTime, duration);
        this.fk_epicTask = fk_epicTask;
    }

    public int getFk_epicTask() {
        return fk_epicTask;
    }

    public void setFk_epicTask(int fk_epicTask) {
        this.fk_epicTask = fk_epicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "fk_epicTask=" + fk_epicTask +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                "} ";
    }

    @Override
    public SubTask copy() {
        return new SubTask(this);
    }
}