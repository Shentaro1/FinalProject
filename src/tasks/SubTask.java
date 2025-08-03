package tasks;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import types.Status;

public class SubTask extends AbstractTask {
    EpicTask fkEpicTask;

    //Конструктор копирования
    public SubTask(SubTask subTask) {
        super(subTask);
        fkEpicTask = subTask.getFk_epicTask();
    }

    //Дефолтный конструктор
    public SubTask(String description, String name, EpicTask fkEpicTask) {
        super(description, name);
        this.fkEpicTask = fkEpicTask;
    }

    //Конструктор для создания при помощи файла
    public SubTask(String name, String description, Status status, int id, EpicTask epicTask) {
        super(id, description, name, status);
        this.fkEpicTask = epicTask;
    }

    //get
    public EpicTask getFk_epicTask() {
        return fkEpicTask;
    }

    //set
    public void setFk_epicTask(EpicTask fkEpicTask) {
        this.fkEpicTask = fkEpicTask;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "fk_epicTask=" + fkEpicTask.getId() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                "} ";
    }

    @Override
    public SubTask copy() {
        return new SubTask(this);
    }
}
