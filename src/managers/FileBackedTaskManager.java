package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import Exception.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    //Конструктор для создания FileBackedTaskManager
    public FileBackedTaskManager(InMemoryTaskManager tm, File file) {
        super(tm);
        this.file = file;
    }

//  public static FileBackedTaskManager loadFromFile(File file) {
////
////    }
    //Переопределённые методы из InMemoryTaskManager
    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubTasks() {
        super.clearSubTasks();
        save();
    }

    @Override
    public void clearEpicTasks() {
        super.clearEpicTasks();
        save();
    }


    @Override
    public int createTask(Task task) {
        int a = super.createTask(task);
        save();
        return a;
    }

    @Override
    public int createSubTask(SubTask subTask) {
        int a = super.createSubTask(subTask);
        save();
        return a;
    }

    @Override
    public int createEpicTask(EpicTask epicTask) {
        int a = super.createEpicTask(epicTask);
        save();
        return a;
    }

    @Override
    public boolean updateTask(Task task) {
        boolean a = super.updateTask(task);
        save();
        return a;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        boolean a = super.updateSubTask(subTask);
        save();
        return a;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        boolean a = super.updateEpicTask(epicTask);
        save();
        return a;
    }

    @Override
    public boolean deleteTaskByID(int id) {
        boolean a = super.deleteTaskByID(id);
        save();
        return a;
    }

    @Override
    public boolean deleteSubTaskByID(int id) {
        boolean a = super.deleteSubTaskByID(id);
        save();
        return a;
    }

    @Override
    public boolean deleteEpicTaskByID(int id) {
        boolean a = super.deleteEpicTaskByID(id);
        save();
        return a;
    }

    //метод сохраняющий в файл
    public void save() {
        try (FileWriter fileWriter = new FileWriter(file, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(CSVFormater.firstLine());
            for (Task task : getAllTask()) {
                bufferedWriter.write(CSVFormater.toStringTask(task));
            }

            for (SubTask task : getAllSubTask()) {
                bufferedWriter.write(CSVFormater.toStringSubTask(task));
            }

            for (EpicTask epicTask : getAllEpicTask()) {
                bufferedWriter.write(CSVFormater.toStringEpicTask(epicTask));
            }

        } catch (IOException ignored) {
            throw new ManagerSaveException(ignored.getMessage());
        }

    }

}
