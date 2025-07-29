package tasks;
import types.Status;

public class Task extends AbstractTask {

    //Дефолтный конструктор
    public Task(String description, String name) {
        super(description, name);
    }

    //Конструктор копирования
    public Task(Task task) {
        super(task);
    }

    //Конструктор для создания при помощи файла
    public Task(String name, String description, Status status, int id) {
        super(id, description, name, status);
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