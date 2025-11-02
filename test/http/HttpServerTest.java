package http;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import tasks.AbstractTask;
import utils.JsonUtil;
import utils.Managers;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

class HttpServerTest {
    int port;
    HttpTaskServer httpTaskServer;
    TaskManager taskManager;
    DateTimeFormatter dateTimeFormatter;
    Gson gson;

    @BeforeEach
    void startHttpTaskServer() throws IOException {
        port = 8083;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        taskManager = Managers.getDefault();
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDateTime.class, JsonUtil.CustomTypeAdapters.ofLocalDateTime(dateTimeFormatter))
                .registerTypeAdapter(Duration.class, JsonUtil.CustomTypeAdapters.ofDuration())
                .create();
        httpTaskServer = new HttpTaskServer(port, taskManager, gson);
        httpTaskServer.start();
    }

    @AfterEach
    void stopHttpTaskServer() {
        httpTaskServer.stop();
    }

    HttpResponse<String> sendHttpRequest(HttpRequest httpRequest) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            return httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void checkEqualTask(AbstractTask<?> actual, AbstractTask<?> expected) {
        assertEquals(actual.getId(), expected.getId(), "Не совпадает id");
        assertEquals(actual.getName(), expected.getName(), "Не совпадает name");
        assertEquals(actual.getDescription(), expected.getDescription(),  "Не совпадает description");
        assertEquals(actual.getDuration(), expected.getDuration(), "Не совпадает duration");
        assertEquals(actual.getStartTime(), expected.getStartTime(), "Не совпадает startTime");
        assertEquals(actual.getStatus(), expected.getStatus(), "Не совпадает status");
    }
}
