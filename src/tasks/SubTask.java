package tasks;
import types.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends AbstractTask<SubTask> {
    private int fkEpicTask;

    public SubTask(String description, String name, int fk_epicTask, LocalDateTime startTime, Duration duration) {
        super(description, name, startTime, duration);
        this.fkEpicTask = fk_epicTask;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        fkEpicTask = subTask.getFkEpicTask();
    }

    public SubTask(String description, String name, int fk_epicTask) {
        super(description, name);
        this.fkEpicTask = fk_epicTask;
    }

    public SubTask(int id, String description, String name, Status status, int fkEpicTask, LocalDateTime startTime, Duration duration) {
        super(id, description, name, status, startTime, duration);
        this.fkEpicTask = fkEpicTask;
    }

    public int getFkEpicTask() {
        return fkEpicTask;
    }

    public void setFkEpicTask(int fkEpicTask) {
        this.fkEpicTask = fkEpicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "fk_epicTask=" + fkEpicTask +
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