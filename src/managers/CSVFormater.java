package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class CSVFormater {

    public static String toStringTask(Task task) {
        return String.format(
                "%d,TASK,%s,%s,%s,\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription());
    }

    public static String toStringSubTask(SubTask task) {
        return String.format(
                "%d,TASK,%s,%s,%s,\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription(),
                task.getFk_epicTask());
    }

    public static String toStringEpicTask(EpicTask task) {
        return String.format(
                "%d,TASK,%s,%s,%s,\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription(),
                task.getSubTasks());
    }

    public static String firstLine() {
        return ("id,type,name,status,description,epic");
    }


}
