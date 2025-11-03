package httpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.EpicTask;
import utils.JsonUtil;
import java.io.IOException;

public final class EpicTaskHandler extends BaseHttpHandler {
    public EpicTaskHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        final int pathLength = path.length;

        try {
            if (requestMethod.equals("GET") && pathLength == 2) {
                String result = JsonUtil.convertToJson(taskManager.getAllEpicTask(), gson);
                sendText(exchange, result);
            } else if (requestMethod.equals("GET") && pathLength == 3) {
                EpicTask task = taskManager.getEpicTaskByID(Integer.parseInt(path[2]));
                sendText(exchange, JsonUtil.convertToJson(task, gson));
            } else if (requestMethod.equals("DELETE") && pathLength == 3) {
                final int id = Integer.parseInt(path[2]);
                EpicTask delTask = taskManager.getEpicTaskByID(id);
                taskManager.deleteEpicTaskByID(id);
                sendText(exchange, JsonUtil.convertToJson(delTask, gson));
            } else if (requestMethod.equals("POST") && pathLength == 3) {
                String body = getBodyRequest(exchange);
                EpicTask task = JsonUtil.convertFromJson(body, EpicTask.class, gson);
                task.setId(Integer.parseInt(path[2]));
                taskManager.updateEpicTask(task);
                sendNotBody(exchange);
            } else if (requestMethod.equals("POST") && pathLength == 2) {
                String body = getBodyRequest(exchange);
                taskManager.createEpicTask(JsonUtil.convertFromJson(body, EpicTask.class, gson));
                sendNotBody(exchange);
            } else if (requestMethod.equals("GET") && pathLength == 4) {
                final int id = Integer.parseInt(path[2]);
                sendText(exchange, JsonUtil.convertToJson(taskManager.getAllSubTaskByEpicTask(id), gson));
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
