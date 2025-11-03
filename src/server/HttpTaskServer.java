package server;
import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import httpHandlers.*;
import managers.TaskManager;
import utils.JsonUtil;
import utils.Managers;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class HttpTaskServer {
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private final HttpServer httpServer;

    public HttpTaskServer(int port, TaskManager taskManager, Gson gson) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicTaskHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }


    public static void main(String[] args) throws IOException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, JsonUtil.CustomTypeAdapters.ofLocalDateTime(dateTimeFormatter))
                .registerTypeAdapter(Duration.class, JsonUtil.CustomTypeAdapters.ofDuration())
                .create();

        HttpTaskServer httpTaskServer = new HttpTaskServer(8080, Managers.getDefault(), gson);
        httpTaskServer.start();
    }

    public void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }
}
