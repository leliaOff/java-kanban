package api;

import app.HttpTaskServer;
import config.App;
import kanban.manager.enums.Status;
import kanban.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HistoryApiTest {
    protected HttpClient client;
    private static final String POINT = "http://localhost:8080";
    @BeforeEach
    public void beforeEach() {
        App.test();
        HttpTaskServer.start();
        client = HttpClient.newBuilder().build();
    }
    @AfterEach
    public void afterEach() {
        HttpTaskServer.stop();
    }

    @Test
    void getHistory() {
        URI url = URI.create(POINT + "/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
        } catch (InterruptedException | IOException e) { 
            Assertions.fail();
        }
    }
}
