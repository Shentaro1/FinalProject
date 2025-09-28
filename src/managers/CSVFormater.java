package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;

public class CSVFormater {

    public static String toStringTask(Task task) {
        return String.format(
                "%d,TASK,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription(),
                task.getStartTime().toString(),
                task.getDuration().toString());
    }

    public static String toStringSubTask(SubTask task) {
        return String.format(
                "%d,SUBTASK,%s,%s,%s,%s,%s,%d\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription(),
                task.getStartTime().toString(),
                task.getDuration().toString(),
                task.getFk_epicTask().getId());
    }

    public static String toStringEpicTask(EpicTask task) {
        return String.format(
                "%d,EPICTASK,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getName(),
                task.getStatus().name().toUpperCase(),
                task.getDescription(),
                task.getStartTime().toString(),
                task.getDuration().toString());
    }

    public static String firstLine() {
        return ("id,type,name,status,description,startTime,duration,epic\n");
    }


}
