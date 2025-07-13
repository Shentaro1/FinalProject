package tasks;
import types.Status;

import java.util.ArrayList;

public class EpicTask extends AbstractTask {
    private final ArrayList<SubTask> subTasks;

    public EpicTask(EpicTask epicTask) {
        super(epicTask);
        subTasks = new ArrayList<>(epicTask.getSubTasks());
    }

    public EpicTask(String description, String name) {
        super(description, name);
        subTasks = new ArrayList<>();
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            status = Status.NEW;
            return;
        }

        boolean isExistStatusNEW = false;
        boolean isExistStatusDONE = false;
        for (SubTask subTask : subTasks) {
            switch (subTask.getStatus()) {
                case IN_PROGRESS:
                    status = Status.IN_PROGRESS;
                    return;
                case NEW:
                    if (isExistStatusDONE) {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    isExistStatusNEW = true;
                    break;
                case DONE:
                    if (isExistStatusNEW) {
                        status = Status.IN_PROGRESS;
                        return;
                    }
                    isExistStatusDONE = true;
                    break;
            }
        }
        if (isExistStatusDONE)
            status = Status.DONE;
        else
            status = Status.NEW;
    }

    //get
    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTasks.size=" + subTasks.size() +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", name='" + getName() + '\'' +
                ", status=" + getStatus() +
                "} ";
    }

    @Override
    public EpicTask copy() {
        return new EpicTask(this);
    }
}