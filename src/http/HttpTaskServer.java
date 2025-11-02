package http;
import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exception.NotFoundException;
import managers.TaskManager;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import utils.JsonUtil;
import utils.Managers;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    abstract static class BaseHttpHandler implements HttpHandler {
        final TaskManager taskManager;
        final Gson gson;

        BaseHttpHandler(TaskManager taskManager, Gson gson) {
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

    static final class TaskHandler extends BaseHttpHandler {
        TaskHandler(TaskManager taskManager, Gson gson) {
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

    static final class SubTaskHandler extends BaseHttpHandler {
        SubTaskHandler(TaskManager taskManager, Gson gson) {
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

    static final class EpicTaskHandler extends BaseHttpHandler {
        EpicTaskHandler(TaskManager taskManager, Gson gson) {
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

    static final class HistoryHandler extends BaseHttpHandler {
        HistoryHandler(TaskManager taskManager, Gson gson) {
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

    static final class PrioritizedHandler extends BaseHttpHandler {
        PrioritizedHandler(TaskManager taskManager, Gson gson) {
            super(taskManager, gson);
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");
            final int pathLength = path.length;

            try {
                if (requestMethod.equals("GET") && pathLength == 2) {
                    String result = JsonUtil.convertToJson(taskManager.getPrioritizedTasks(), gson);
                    sendText(exchange, result);
                } else {
                    sendNotFound(exchange);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
