package httpHandlers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.EpicTask;
import tasks.SubTask;
import tasks.Task;
import types.Status;
import utils.JsonUtil;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SubTasksHttpServerTest extends HttpServerTest {
    @Test
    void getSubTasks() throws NotFoundException {
        SubTask task = new SubTask(1, "a", "b", Status.NEW, 0,null, null);
        taskManager.createEpicTask(new EpicTask("a", "b"));
        taskManager.createSubTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/subtasks"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидался JsonArray");
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        checkEqualTask(JsonUtil.convertFromJson(jsonObject.toString(), Task.class, gson), task);
    }

    @Test
    void getSubTasksById() throws NotFoundException {
        SubTask task = new SubTask(1, "a", "b", Status.NEW, 0,null, null);
        taskManager.createEpicTask(new EpicTask("a", "b"));
        taskManager.createSubTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/subtasks/1"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");

        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), SubTask.class, gson), task);
    }

    @Test
    void deleteSubTasksById() throws NotFoundException {
        SubTask task = new SubTask(1, "a", "b", Status.NEW, 0,null, null);
        taskManager.createEpicTask(new EpicTask("a", "b"));
        taskManager.createSubTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:" + port + "/subtasks/1"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        assertTrue(taskManager.getAllSubTask().isEmpty(), "subtask не был удален");
        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), SubTask.class, gson), task);
    }

    @Test
    void createSubTasks() throws NotFoundException {
        SubTask task = new SubTask(1, "a", "b", Status.NEW, 0,null, null);
        taskManager.createEpicTask(new EpicTask("a", "b"));
        taskManager.createSubTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/subtasks"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        assertFalse(taskManager.getAllSubTask().isEmpty(), "subtask не был создан");
        SubTask createTask = taskManager.getSubTaskByID(1);
        checkEqualTask(createTask, task);
    }

    @Test
    void updateTasks() throws NotFoundException {
        SubTask task = new SubTask(1, "a", "b", Status.NEW, 0,null, null);
        taskManager.createEpicTask(new EpicTask("a", "b"));
        taskManager.createSubTask(task);
        task.setStatus(Status.DONE);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/subtasks/1"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        checkEqualTask(taskManager.getSubTaskByID(1), task);
    }
}
