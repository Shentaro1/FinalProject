package httpHandlers;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    final TaskManager taskManager;
    final Gson gson;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    static String getBodyRequest(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            return new String(in.readAllBytes(), CHARSET);
        }
    }


    void sendText(HttpExchange exchange, String body) throws IOException {
        exchange.sendResponseHeaders(200, 0);

        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body.getBytes(StandardCharsets.UTF_8));
        }
    }

    void sendNotBody(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, -1);
    }

    void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, -1);
    }

    void sendHasOverlaps(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, -1);
    }
}
