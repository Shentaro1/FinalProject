package tasks;
import types.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractTask<T extends AbstractTask<T>> {
    private int id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    private final String typeName;

    AbstractTask(AbstractTask<T> abstractTask) {
        this(abstractTask.id, abstractTask.description, abstractTask.name, abstractTask.status, abstractTask.startTime, abstractTask.duration);
    }

    AbstractTask(int id, String description, String name, Status status) {
        this(id, description, name, status,null, null);
    }

    AbstractTask(String description, String name) {
        this(0, description, name, Status.NEW, null, null);
    }

    AbstractTask(String description, String name, LocalDateTime startTime, Duration duration) {
        this(0, description, name, Status.NEW, startTime, duration);
    }

    AbstractTask(int id, String description, String name, Status status, LocalDateTime startTime, Duration duration) {
        this.description = description;
        this.name = name;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.typeName = this.getClass().getSimpleName();
    }

    //set
    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    //get
    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return (startTime == null || duration == null) ? null : startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractTask<?> that)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    abstract public T copy();

    @Override
    public String toString() {
        return "AbstractTask{" +
                "description='" + description + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
