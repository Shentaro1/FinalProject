package managers;
import exception.ManagerSaveException;
import exception.NotFoundException;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.Status;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    private final Charset encoding = StandardCharsets.UTF_8;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    private FileBackedTaskManager(InMemoryTaskManager donor, File file) {
        super(donor);
        this.file = file;
    }

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
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createSubTask(SubTask subTask) throws NotFoundException {
        int id = super.createSubTask(subTask);
        save();
        return id;
    }

    @Override
    public int createEpicTask(EpicTask epicTask) {
        int id = super.createEpicTask(epicTask);
        save();
        return id;
    }

    @Override
    public int updateTask(Task task) throws NotFoundException {
        int codeError = super.updateTask(task);
        save();
        return codeError;
    }

    @Override
    public int updateSubTask(SubTask subTask) throws NotFoundException {
        int codeError = super.updateSubTask(subTask);
        save();
        return codeError;
    }

    @Override
    public int updateEpicTask(EpicTask epicTask) throws NotFoundException {
        int codeError = super.updateEpicTask(epicTask);
        save();
        return codeError;
    }

    @Override
    public boolean deleteTaskByID(int id) {
        boolean state = super.deleteTaskByID(id);
        save();
        return state;
    }

    @Override
    public boolean deleteSubTaskByID(int id) {
        boolean state = super.deleteSubTaskByID(id);
        save();
        return state;
    }

    @Override
    public boolean deleteEpicTaskByID(int id) {
        boolean state = super.deleteEpicTaskByID(id);
        save();
        return state;
    }

    private void save() {
        try (
                FileWriter writer = new FileWriter(file, encoding, false);
                BufferedWriter bf = new BufferedWriter(writer);
        ) {
            bf.write("id,type,name,status,description,epic,startTime,duration\n");

            for (Task task : getAllTask()) {
                bf.write(String.format(
                                "%d,TASK,%s,%s,%s,,%s,%s\n",
                                task.getId(),
                                task.getName(),
                                task.getStatus().name().toUpperCase(),
                                task.getDescription(),
                                task.getStartTime() != null ? task.getStartTime() : "",
                                task.getDuration() != null ? task.getDuration().toMinutes() : ""
                        )
                );
            }

            for (EpicTask epicTask : getAllEpicTask()) {
                bf.write(String.format(
                                "%d,EPIC,%s,%s,%s,,%s,%s\n",
                                epicTask.getId(),
                                epicTask.getName(),
                                epicTask.getStatus().name().toUpperCase(),
                                epicTask.getDescription(),
                                epicTask.getStartTime() != null ? epicTask.getStartTime() : "",
                                epicTask.getDuration() != null ? epicTask.getDuration().toMinutes() : ""
                        )
                );
            }

            for (SubTask subTask : getAllSubTask()) {
                bf.write(String.format(
                                "%d,SUBTASK,%s,%s,%s,%s,%s,%s\n",
                                subTask.getId(),
                                subTask.getName(),
                                subTask.getStatus().name().toUpperCase(),
                                subTask.getDescription(),
                                subTask.getFkEpicTask(),
                                subTask.getStartTime() != null ? subTask.getStartTime() : "",
                                subTask.getDuration() != null ? subTask.getDuration().toMinutes() : ""
                        )
                );
            }

        } catch (IOException ex) {
            throw new ManagerSaveException(ex.getMessage());
        }
    }

    static public FileBackedTaskManager loadFromFile (File file) {
        InMemoryTaskManager tm = new InMemoryTaskManager(new InMemoryHistoryManager());
        int maxId = -1;

        try (
                FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
                BufferedReader bf = new BufferedReader(reader);
        ) {
            bf.readLine();

            while (bf.ready()) {
                String[] record = new String[8];
                String[] dp = bf.readLine().split(",");
                System.arraycopy(dp, 0, record, 0, dp.length);

                int currentId = Integer.parseInt(record[0]);
                maxId = Math.max(maxId, currentId);
                tm.setCounterID(currentId);
                try {
                    switch (record[1]) {
                        case "TASK":
                            tm.createTask(new Task(
                                            currentId,
                                            record[4],
                                            record[2],
                                            Status.valueOf(record[3]),
                                            record[6] != null ? LocalDateTime.parse(record[6]) : null,
                                            record[7] != null ? Duration.ofMinutes(Integer.parseInt(record[7])): null
                                    )
                            );
                            break;
                        case "SUBTASK":
                            tm.createSubTask(new SubTask(
                                            currentId,
                                            record[4],
                                            record[2],
                                            Status.valueOf(record[3]),
                                            Integer.parseInt(record[5]),
                                            record[6] != null ? LocalDateTime.parse(record[6]) : null,
                                            record[7] != null ? Duration.ofMinutes(Integer.parseInt(record[7])): null
                                    )
                            );
                            break;
                        case "EPIC":
                            tm.createEpicTask(new EpicTask(
                                            currentId,
                                            record[4],
                                            record[2],
                                            Status.valueOf(record[3])
                                    )
                            );
                            break;
                    }
                } catch (DateTimeParseException | NotFoundException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());;
        }

        tm.setCounterID(maxId + 1);
        return new FileBackedTaskManager(tm, file);
    }

}