package http;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.EpicTask;
import types.Status;
import utils.JsonUtil;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EpicsHttpServerTest extends HttpServerTest {
    @Test
    void getEpics() {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);
        taskManager.createEpicTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидался JsonArray");
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        checkEqualTask(JsonUtil.convertFromJson(jsonObject.toString(), EpicTask.class, gson), task);
    }

    @Test
    void getEpicsById() {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);
        taskManager.createEpicTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/epics/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");

        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), EpicTask.class, gson), task);
    }

    @Test
    void deleteEpicsById() {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);
        taskManager.createEpicTask(task);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:" + port + "/epics/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        assertTrue(taskManager.getAllTask().isEmpty(), "epicTask не был удален");
        checkEqualTask(JsonUtil.convertFromJson(httpResponse.body(), EpicTask.class, gson), task);
    }

    @Test
    void createEpics() throws NotFoundException {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/epics"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        assertFalse(taskManager.getAllEpicTask().isEmpty(), "epicTask не был создан");
        EpicTask createTask = taskManager.getEpicTaskByID(0);
        checkEqualTask(createTask, task);
    }

    @Test
    void updateEpics() throws NotFoundException {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);
        taskManager.createEpicTask(task);
        task.setName("new name");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.convertToJson(task, gson)))
                .uri(URI.create("http://localhost:" + port + "/epics/0"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(201, httpResponse.statusCode(), "Статус код не равен 201");
        checkEqualTask(taskManager.getEpicTaskByID(0), task);
    }
}