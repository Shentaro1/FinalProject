package tasks;
import types.Status;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task extends AbstractTask {

    //Дефолтный конструктор со временем
    public Task(String description, String name) {
        super(description, name);
    }

    //Конструктор копирования
    public Task(Task task) {
        super(task);
    }

    //Конструктор создания при помощи файла с временем
    public Task(String name, String description, Status status, int id, Duration duration, LocalDateTime startTime) {
        super(id, duration, startTime, name, description, status);
    }

    @Override
    public LocalDateTime getEndTime() {
        if (getStartTime() == null || getDuration() == null) {
            return null;
        }
        return getStartTime().plus(getDuration());
    }


    //set
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                "} ";
    }

    @Override
    public Task copy() {
        return new Task(this);
    }

}