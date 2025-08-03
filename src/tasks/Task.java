package tasks;
import types.Status;

public class Task extends AbstractTask {

    public Task(String description, String name) {
        super(description, name);
    }

    public Task(Task task) {
        super(task);
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
                "} ";
    }

    @Override
    public Task copy() {
        return new Task(this);
    }
}