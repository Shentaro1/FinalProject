package utils;

import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.*;
import types.Status;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedMain {
    public static void main(String[] args) {
        File file = new File("Testfw34");
        TaskManager tm = Managers.getDefaultFileBack(file);
        LocalDateTime test = LocalDateTime.now();

        tm.createTask(new Task("a", "b", Status.NEW, 28, Duration.ofNanos(1), test));
        tm.createEpicTask(new EpicTask("fghjk", "fghjkloi8uytgbhnj"));
        tm.createSubTask(new SubTask("subTask", "name", tm.getAllEpicTask().getFirst()));
        tm.createTask(new Task("ertyuiop", " r567890okijm", Status.NEW, 48, Duration.ofNanos(1), LocalDateTime.now()));
//        Task task = tm.getAllTask().get(1);
//        task.setStartTime(LocalDateTime.now());

        TaskManager tmb = FileBackedTaskManager.loadFromFile(file);
        System.out.println(tmb.getAllTask());
        System.out.println(tmb.getAllSubTask());
        System.out.println(tmb.getAllEpicTask());
        tm.getPrioritizedTasks();
   }
}
