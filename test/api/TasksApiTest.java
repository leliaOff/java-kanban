package api;

import app.HttpTaskServer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.App;
import kanban.manager.exceptions.ManagerDeleteException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TasksApiTest {
    protected HttpClient client;
    private static final String POINT = "http://localhost:8080";

    @BeforeEach
    public void beforeEach() {
        App.test();
        HttpTaskServer.start();
        client = HttpClient.newBuilder().build();
        createTask("aaa", "aaa", "14.03.2024 09:00:00");
        createTask("bbb", "bbb", "14.03.2024 10:00:00");
        createTask("ccc", "ccc", "14.03.2024 10:30:00");
        createTask("ddd", "ddd", "14.03.2024 11:00:00");
    }

    @AfterEach
    public void afterEach() {
        HttpTaskServer.stop();
        try {
            Files.deleteIfExists(Paths.get(App.getTaskFilename()));
            Files.deleteIfExists(Paths.get(App.getHistoryFilename()));
        } catch (IOException exception) {
            throw new ManagerDeleteException(exception.getMessage());
        }
    }

    @Test
    void getTasks() {
        URI url = URI.create(POINT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            Assertions.assertTrue(jsonElement.isJsonArray());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void getTask() {
        URI url = URI.create(POINT + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            Assertions.assertTrue(jsonElement.isJsonObject());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            Assertions.assertEquals(1, jsonTask.get("id").getAsInt());
            Assertions.assertEquals("aaa", jsonTask.get("title").getAsString());
            Assertions.assertEquals("aaa", jsonTask.get("description").getAsString());
            Assertions.assertEquals("NEW", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void getNotExistTask() {
        URI url = URI.create(POINT + "/tasks/11");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(404, response.statusCode());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void storeTask() {
        URI url = URI.create(POINT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"eee\",\"description\":\"eee\",\"startTime\":\"17.03.2024 09:00:00\",\"duration\":3600}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            Assertions.assertTrue(jsonElement.isJsonObject());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            Assertions.assertEquals("eee", jsonTask.get("title").getAsString());
            Assertions.assertEquals("eee", jsonTask.get("description").getAsString());
            Assertions.assertEquals("NEW", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void storeIntersectTask() {
        URI url = URI.create(POINT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"eee\",\"description\":\"eee\",\"startTime\":\"14.03.2024 08:30:00\",\"duration\":3600}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(406, response.statusCode());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void updateTask() {
        int id = createTask("eee", "eee", "15.03.2024 09:00:00");
        URI url = URI.create(POINT + "/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"fff\",\"status\":\"IN_PROGRESS\"}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(200, response.statusCode());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            Assertions.assertTrue(jsonElement.isJsonObject());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            Assertions.assertEquals(id, jsonTask.get("id").getAsInt());
            Assertions.assertEquals("fff", jsonTask.get("title").getAsString());
            Assertions.assertEquals("eee", jsonTask.get("description").getAsString());
            Assertions.assertEquals("IN_PROGRESS", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void deleteTask() {
        int id = createTask("eee", "eee", "16.03.2024 09:00:00");
        URI url = URI.create(POINT + "/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void deleteNotExistTask() {
        URI url = URI.create(POINT + "/tasks/11");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(404, response.statusCode());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    protected int createTask(String name, String description, String time) {
        URI url = URI.create(POINT + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"" + name +
                        "\",\"description\":\"" + description +
                        "\",\"startTime\":\"" + time +
                        "\",\"duration\":3600}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            return jsonTask.get("id").getAsInt();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }
}
