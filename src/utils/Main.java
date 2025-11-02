package utils;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;


public class Main {
    public static void main(String[] args) throws NotFoundException {
        TaskManager tm = Managers.getDefault();

        //target: создание 2 x Task
        Task task1 = new Task("a", "b");
        Task task2 = new Task("c", "d");
        tm.createTask(task1);
        tm.createTask(task2);

        //target: создание 2 x EpicTask
        EpicTask epicTask = new EpicTask("a", "b");
        EpicTask eppicTask = new EpicTask("c", "d");
        int id_epicTaskk = tm.createEpicTask(epicTask);
        tm.createEpicTask(eppicTask);

        //target: создание 3 x SubTask c привязкой к одному EpicTask
        SubTask subTask = new SubTask("a", "b", id_epicTaskk);
        SubTask subTaskTow = new SubTask("c", "d", id_epicTaskk);
        SubTask subTaskThree = new SubTask("e", "f", id_epicTaskk);
        tm.createSubTask(subTask);
        tm.createSubTask(subTaskTow);
        tm.createSubTask(subTaskThree);

        //target: 1 запрос (пустой)
        System.out.println(tm.getHistory());

        tm.getTaskByID(0);
        tm.getTaskByID(1);
        tm.getEpicTaskByID(2);
        tm.getEpicTaskByID(3);
        tm.getSubTaskByID(4);
        tm.getSubTaskByID(5);
        tm.getSubTaskByID(6);

        //target: 2 запрос (последовательный)
        System.out.println(tm.getHistory());

        tm.deleteTaskByID(0);

        //target: 3 запрос (удаление Task)
        System.out.println(tm.getHistory());

        tm.deleteEpicTaskByID(id_epicTaskk);

        //target: 3 запрос (удаление EpicTask c 3 x SubTask)
        System.out.println(tm.getHistory());
    }
}
