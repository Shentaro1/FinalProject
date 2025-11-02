package tasks;
import types.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task extends AbstractTask<Task> {

    public Task(String description, String name) {
        super(description, name);
    }

    public Task(Task task) {
        super(task);
    }

    public Task(int id, String description, String name, Status status) {
        super(id, description, name, status);
    }

    public Task(int id, String description, String name, Status status, LocalDateTime startTime, Duration duration) {
        super(id, description, name, status, startTime, duration);
    }

    public Task(String description, String name, LocalDateTime startTime, Duration duration) {
        super(description, name, startTime, duration);
    }

    @Override
    public Task copy() {
        return new Task(this);
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                "} ";
    }
}