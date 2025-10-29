package tasks;
import types.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractTask implements Comparable<AbstractTask> {
    private int id;
    private Duration duration;
    private LocalDateTime startTime;
    private String name;
    private String description;
    protected Status status;

    //конструктор копирования
    AbstractTask(AbstractTask abstractTask) {
        this(abstractTask.id, abstractTask.duration, abstractTask.startTime, abstractTask.name, abstractTask.description, abstractTask.status);
    }

    //дефолтный конструктор создания
    AbstractTask(String description, String name) {
        this.id = 0;
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = null;
        this.startTime = null;
    }

    //конструктор для сохранения из файла со временем
    public AbstractTask(int id, Duration duration, LocalDateTime startTime, String name, String description, Status status) {
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        this.name = name;
        this.description = description;
        this.status = status;
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

    //метод расчитывающий дату и время завершения задачи
    public abstract LocalDateTime getEndTime();

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractTask)) return false;
        AbstractTask that = (AbstractTask) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public abstract AbstractTask copy();

    @Override
    public int compareTo(AbstractTask o) {
        if (this.getStartTime().isBefore(o.getStartTime())) {
            return 1;
        }
        if (this.getStartTime().isAfter(o.getStartTime())) {
            return -1;
        } else {
            return 0;
        }

    }

}
