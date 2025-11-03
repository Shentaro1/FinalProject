package httpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.Task;
import utils.JsonUtil;
import java.io.IOException;

public final class TaskHandler extends BaseHttpHandler {
    public TaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        final int pathLength = path.length;

        try {
            if (requestMethod.equals("GET") && pathLength == 2) {
                String result = JsonUtil.convertToJson(taskManager.getAllTask(), gson);
                sendText(exchange, result);
            } else if (requestMethod.equals("GET") && pathLength == 3) {
                Task task = taskManager.getTaskByID(Integer.parseInt(path[2]));
                sendText(exchange, JsonUtil.convertToJson(task, gson));
            } else if (requestMethod.equals("DELETE") && pathLength == 3) {
                final int id = Integer.parseInt(path[2]);
                Task delTask = taskManager.getTaskByID(id);
                taskManager.deleteTaskByID(id);
                sendText(exchange, JsonUtil.convertToJson(delTask, gson));
            } else if (requestMethod.equals("POST") && pathLength == 3) {
                String body = getBodyRequest(exchange);
                Task task = JsonUtil.convertFromJson(body, Task.class, gson);
                task.setId(Integer.parseInt(path[2]));
                final int codeError = taskManager.updateTask(task);

                if (codeError == -3)
                    sendHasOverlaps(exchange);
                else
                    sendNotBody(exchange);
            } else if (requestMethod.equals("POST") && pathLength == 2) {
                String body = getBodyRequest(exchange);
                final int taskId = taskManager.createTask(JsonUtil.convertFromJson(body, Task.class, gson));

                if (taskId == -3)
                    sendHasOverlaps(exchange);
                else
                    sendNotBody(exchange);
            } else {
                sendNotFound(exchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
