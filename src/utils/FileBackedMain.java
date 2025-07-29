package utils;

import managers.FileBackedTaskManager;
import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import tasks.*;

import java.io.File;

public class FileBackedMain {
    public static void main(String[] args) {
        File file = new File("Testfw34");
        InMemoryHistoryManager<AbstractTask> hm = new InMemoryHistoryManager<>();
        TaskManager tm = new TaskManager() {
        }
        FileBackedTaskManager fbtm = new FileBackedTaskManager(tm, file);

        tm.createTask(new Task("a", "b"));
        tm.createTask(new Task("vffff", "grsfd"));

//        TaskManager tmb = FileBackedTaskManager.loadFromFile(file);
//
//        System.out.println(tmb.getAllTask());
//        System.out.println(tmb.getAllSubTask());
//        System.out.println(tmb.getAllEpicTask());
    }
}
