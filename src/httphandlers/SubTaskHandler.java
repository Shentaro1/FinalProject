package httphandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.SubTask;
import utils.JsonUtil;
import java.io.IOException;

public final class SubTaskHandler extends BaseHttpHandler {
    public SubTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        final int pathLength = path.length;

        try {
            if (requestMethod.equals("GET") && pathLength == 2) {
                String result = JsonUtil.convertToJson(taskManager.getAllSubTask(), gson);
                sendText(exchange, result);
            } else if (requestMethod.equals("GET") && pathLength == 3) {
                SubTask task = taskManager.getSubTaskByID(Integer.parseInt(path[2]));
                sendText(exchange, JsonUtil.convertToJson(task, gson));
            } else if (requestMethod.equals("DELETE") && pathLength == 3) {
                final int id = Integer.parseInt(path[2]);
                SubTask delTask = taskManager.getSubTaskByID(id);
                taskManager.deleteSubTaskByID(id);
                sendText(exchange, JsonUtil.convertToJson(delTask, gson));
            } else if (requestMethod.equals("POST") && pathLength == 3) {
                String body = getBodyRequest(exchange);
                SubTask task = JsonUtil.convertFromJson(body, SubTask.class, gson);
                task.setId(Integer.parseInt(path[2]));
                final int codeError = taskManager.updateSubTask(task);

                if (codeError == -3)
                    sendHasOverlaps(exchange);
                else
                    sendNotBody(exchange);
            } else if (requestMethod.equals("POST") && pathLength == 2) {
                String body = getBodyRequest(exchange);
                final int taskId = taskManager.createSubTask(JsonUtil.convertFromJson(body, SubTask.class, gson));

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