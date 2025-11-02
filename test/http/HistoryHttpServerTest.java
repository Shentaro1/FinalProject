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

public class HistoryHttpServerTest extends HttpServerTest {
    @Test
    void getHistory() throws NotFoundException {
        EpicTask task = new EpicTask(0, "a", "b", Status.NEW, null, null);
        taskManager.createEpicTask(task);
        taskManager.getEpicTaskByID(0);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:" + port + "/history"))
                .build();

        HttpResponse<String> httpResponse = sendHttpRequest(httpRequest);

        assertEquals(200, httpResponse.statusCode(), "Статус код не равен 200");
        JsonElement jsonElement = JsonParser.parseString(httpResponse.body());
        assertTrue(jsonElement.isJsonArray(), "Ожидался JsonArray");
        JsonObject jsonObject = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
        assertEquals("EpicTask", jsonObject.get("typeName").getAsString(), "Ожидался EpicTask");
        checkEqualTask(JsonUtil.convertFromJson(jsonObject.toString(), EpicTask.class, gson), task);
    }
}