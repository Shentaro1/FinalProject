package httphandlers;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import tasks.Task;
import types.Status;
import utils.JsonUtil;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

public class PrioritizedHttpServerTest extends HttpServerTest {
    @Test
    void getPrioritizedList() {
        Task task = new Task(
                0,
                "a",
                "b",
                Status.NEW,
                LocalDateTime.of(
                        2025,
                        1,
                        1,
                        12,
                        0
                ),
                Duration.ofMinutes(30));
        taskManager.createTask(task);

        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Ожидался размер списка равный 1");

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/prioritized"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);
        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидался JsonArray");
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        assertEquals("Task", jsonObject.get("typeName").getAsString(), "Ожидался Task");
        checkEqualTask(JsonUtil.convertFromJson(jsonObject.toString(), Task.class, gson), task);
    }
}