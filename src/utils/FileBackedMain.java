package utils;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.*;

import java.io.File;

public class FileBackedMain {
    public static void main(String[] args) {
        File file = new File("Testfw34");
        TaskManager tm = Managers.getDefaultFileBack(file);

        tm.createTask(new Task("a", "b"));
        tm.createEpicTask(new EpicTask("fghjk", "fghjkloi8uytgbhnj"));
        tm.createTask(new Task("ertyuiop", " r567890okijm"));
        tm.createSubTask(new SubTask("subTask", "name", tm.getEpicTaskByID(1)));

        TaskManager tmb = FileBackedTaskManager.loadFromFile(file);
        System.out.println(tmb.getAllTask());
        System.out.println(tmb.getAllSubTask());
        System.out.println(tmb.getAllEpicTask());
   }
}
