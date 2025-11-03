package httpHandlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import utils.JsonUtil;
import java.io.IOException;

public final class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] path = exchange.getRequestURI().getPath().split("/");
        final int pathLength = path.length;

        try {
            if (requestMethod.equals("GET") && pathLength == 2) {
                String result = JsonUtil.convertToJson(taskManager.getHistory(), gson);
                sendText(exchange, result);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
