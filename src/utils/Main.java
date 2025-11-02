package example;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import utils.Managers;


public class Main {
    public static void main(String[] args) throws NotFoundException {
        TaskManager tm = Managers.getDefault();

        //target: создание 2 x Task
        Task task1 = new Task("a", "b");
        Task task2 = new Task("c", "d");
        tm.createTask(task1);
        tm.createTask(task2);

        //target: создание 2 x EpicTask
        EpicTask epicTask1 = new EpicTask("a", "b");
        EpicTask epicTask2 = new EpicTask("c", "d");
        int id_epicTask1 = tm.createEpicTask(epicTask1);
        tm.createEpicTask(epicTask2);

        //target: создание 3 x SubTask c привязкой к одному EpicTask
        SubTask subTask1 = new SubTask("a", "b", id_epicTask1);
        SubTask subTask2 = new SubTask("c", "d", id_epicTask1);
        SubTask subTask3 = new SubTask("e", "f", id_epicTask1);
        tm.createSubTask(subTask1);
        tm.createSubTask(subTask2);
        tm.createSubTask(subTask3);

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

        tm.deleteEpicTaskByID(id_epicTask1);

        //target: 3 запрос (удаление EpicTask c 3 x SubTask)
        System.out.println(tm.getHistory());
    }
}
