package tasks;
import types.Status;

public class SubTask extends AbstractTask {
    EpicTask fk_epicTask;

    public SubTask(SubTask subTask) {
        super(subTask);
        fk_epicTask = subTask.getFk_epicTask();
    }

    public SubTask(String description, String name, EpicTask fk_epicTask) {
        super(description, name);
        this.fk_epicTask = fk_epicTask;
    }

    //get
    public EpicTask getFk_epicTask() {
        return fk_epicTask;
    }

    //set
    public void setFk_epicTask(EpicTask fk_epicTask) {
        this.fk_epicTask = fk_epicTask;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "fk_epicTask=" + fk_epicTask.getId() +
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
