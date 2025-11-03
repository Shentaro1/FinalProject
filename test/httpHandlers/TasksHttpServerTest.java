package httpHandlers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.Task;
import types.Status;
import utils.JsonUtil;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TasksHttpServerTest extends HttpServerTest {
    @Test
    void getTasks() {
        Task task = new Task(0, "a", "b", Status.NEW, null, null);
        taskManager.createTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидался JsonArray");
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        checkEqualTask(JsonUtil.convertFromJson(jsonObject.toString(), Task.class, gson), task);
    }

    @Test
    void getTasksById() {
        Task task = new Task(0, "a", "b", Status.NEW, null, null);
        taskManager.createTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/tasks/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");

        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), Task.class, gson), task);
    }

    @Test
    void deleteTasksById() {
        Task task = new Task(0, "a", "b", Status.NEW, null, null);
        taskManager.createTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:" + port + "/tasks/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        assertTrue(taskManager.getAllTask().isEmpty(), "task не был удален");
        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), Task.class, gson), task);
    }

    @Test
    void createTasks() throws NotFoundException {
        Task task = new Task(0, "a", "b", Status.NEW, null, null);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/tasks"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        assertFalse(taskManager.getAllTask().isEmpty(), "task не был создан");
        Task createTask = taskManager.getTaskByID(0);
        checkEqualTask(createTask, task);
    }

    @Test
    void updateTasks() throws NotFoundException {
        Task task = new Task(0, "a", "b", Status.NEW, null, null);
        taskManager.createTask(task);
        task.setStatus(Status.DONE);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/tasks/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        checkEqualTask(taskManager.getTaskByID(0), task);
    }
}