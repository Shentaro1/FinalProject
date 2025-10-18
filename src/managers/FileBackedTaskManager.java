package managers;

import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import exception.*;
import types.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    //Конструктор для создания FileBackedTaskManager
    public FileBackedTaskManager(InMemoryTaskManager tm, File file) {
        super(tm);
        this.file = file;
    }

    //Метод загрузки с помощью файла
    public static FileBackedTaskManager loadFromFile(File file) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager(new InMemoryHistoryManager<>());
        int maxId = -1;

        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                String[] myArrayFromLine = new String[8];
                String[] dp = bufferedReader.readLine().split(",");
                System.arraycopy(dp, 0, myArrayFromLine, 0, dp.length);
                int currentID = Integer.parseInt(myArrayFromLine[0]);
                maxId = Math.max(currentID, maxId);
                taskManager.setCounterID(currentID);
                switch (myArrayFromLine[1]) {
                    case "TASK":
                        taskManager.createTask(
                                new Task(
                                        myArrayFromLine[2],
                                        myArrayFromLine[4],
                                        Status.valueOf(myArrayFromLine[3]),
                                        currentID,
                                        Duration.parse(myArrayFromLine[6]),
                                        LocalDateTime.parse(myArrayFromLine[5])
                                )
                        );
                        break;
                    case "EPICTASK":
                        taskManager.createEpicTask(
                                new EpicTask(
                                        myArrayFromLine[2],
                                        myArrayFromLine[4],
                                        Status.valueOf(myArrayFromLine[3]),
                                        currentID,
                                        myArrayFromLine[6] != null ? Duration.parse(myArrayFromLine[6]) : null,
                                        myArrayFromLine[5] != null ? LocalDateTime.parse(myArrayFromLine[5]) : null
                                )
                        );
                        break;
                    case "SUBTASK":
                        taskManager.createSubTask(
                             new SubTask(
                                     myArrayFromLine[2],
                                     myArrayFromLine[4],
                                     Status.valueOf(myArrayFromLine[3]),
                                     currentID,
                                     taskManager.getEpicTaskByID(Integer.parseInt(myArrayFromLine[5])),
                                     myArrayFromLine[7] != null ? Duration.parse(myArrayFromLine[7]) : null,
                                     myArrayFromLine[6] != null ? LocalDateTime.parse(myArrayFromLine[6]) : null
                             )
                        );
                        break;

                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }

        taskManager.setCounterID(maxId + 1);
        return new FileBackedTaskManager(taskManager, file);
    }

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
    private void save() {
        try (FileWriter fileWriter = new FileWriter(file, false);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(CSVFormater.firstLine());

            getAllTask().stream()
                    .map(CSVFormater::toStringTask)
                    .forEach(line -> {
                        try {
                            bufferedWriter.write(line);
                        } catch (IOException e) {
                            throw new ManagerSaveException(e.getMessage());
                        }
                    });

            getAllEpicTask().stream()
                    .map(CSVFormater::toStringEpicTask)
                    .forEach(line -> {
                        try {
                            bufferedWriter.write(line);
                        } catch (IOException e) {
                            throw new ManagerSaveException(e.getMessage());
                        }
                    });

            getAllSubTask().stream()
                    .map(CSVFormater::toStringSubTask)
                    .forEach(line -> {
                        try {
                            bufferedWriter.write(line);
                        } catch (IOException e) {
                            throw new ManagerSaveException(e.getMessage());
                        }
                    });

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }
}
