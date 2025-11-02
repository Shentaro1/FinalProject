package utils;
import exception.NotFoundException;
import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.*;
import java.io.File;

public class FileBackedMain {
    public static void main(String[] args) throws NotFoundException {
        File file = new File("test.csv");
        TaskManager tm = Managers.getFileBackedManager(file);

        tm.createTask(new Task("a", "b"));
        int epicId = tm.createEpicTask(new EpicTask("c", "d"));
        tm.createSubTask(new SubTask("e","f", epicId));

        TaskManager tmb = FileBackedTaskManager.loadFromFile(file);

        System.out.println(tmb.getAllTask());
        System.out.println(tmb.getAllSubTask());
        System.out.println(tmb.getAllEpicTask());
    }
}
