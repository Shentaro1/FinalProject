package tasks;
import types.Status;
import java.util.Objects;

public abstract class AbstractTask {
    private int id;
    private String name;
    private String description;
    protected Status status;

    AbstractTask(AbstractTask abstractTask) {
        this(abstractTask.id, abstractTask.description, abstractTask.name, abstractTask.status);
    }

    AbstractTask(int id, String description, String name, Status status) {
        this.description = description;
        this.name = name;
        this.id = id;
        this.status = status;
    }

    AbstractTask(String description, String name) {
        this(0, description, name, Status.NEW);
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

    public

    @Override
    boolean equals(Object o) {
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
}
