package api;

import app.HttpTaskServer;
import config.App;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PrioritizedApiTest {
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
    void getPrioritized() {
        URI url = URI.create(POINT + "/prioritized");
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
