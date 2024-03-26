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

public class EpicsApiTest {
    protected HttpClient client;
    private static final String POINT = "http://localhost:8080";
    @BeforeEach
    public void beforeEach() {
        App.test();
        HttpTaskServer.start();
        client = HttpClient.newBuilder().build();
        int epicId = createEpic("aaa", "aaa");
        System.out.println(createSubtask("aaa a", "a", "14.03.2024 10:00:00", epicId));
        System.out.println(createSubtask("aaa b", "b", "14.03.2024 11:00:00", epicId));
        System.out.println(createSubtask("aaa c", "c", "14.03.2024 12:00:00", epicId));
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
    void getEpics() {
        URI url = URI.create(POINT + "/epics");
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
    void getEpic() {
        URI url = URI.create(POINT + "/epics/1");
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
            Assertions.assertEquals("14.03.2024 10:00:00", jsonTask.get("startTime").getAsString());
            Assertions.assertEquals("14.03.2024 13:00:00", jsonTask.get("endTime").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void getNotExistEpic() {
        URI url = URI.create(POINT + "/epics/66");
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
    void storeEpic() {
        URI url = URI.create(POINT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"eee\",\"description\":\"eee\"}"))
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
    void updateEpic() {
        int id = createEpic("eee", "eee");
        URI url = URI.create(POINT + "/epics/" + id);
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
            Assertions.assertEquals("NEW", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void updateEpicStatus() {
        int epicId = createEpic("eee", "eee");
        int subtaskId1 = createSubtask("aaa", "bbb", "17.03.2024 09:00:00", epicId);
        int subtaskId2 = createSubtask("bbb", "bbb", "17.03.2024 10:00:00", epicId);
        updateSubtaskStatus(subtaskId1, "IN_PROGRESS");

        URI url = URI.create(POINT + "/epics/" + epicId);
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
            Assertions.assertEquals("IN_PROGRESS", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }

        updateSubtaskStatus(subtaskId1, "DONE");
        url = URI.create(POINT + "/epics/" + epicId);
        request = HttpRequest.newBuilder()
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
            Assertions.assertEquals("IN_PROGRESS", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }

        updateSubtaskStatus(subtaskId2, "DONE");
        url = URI.create(POINT + "/epics/" + epicId);
        request = HttpRequest.newBuilder()
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
            Assertions.assertEquals("DONE", jsonTask.get("status").getAsString());
        } catch (InterruptedException | IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void deleteEpic() {
        int id = createEpic("eee", "eee");
        URI url = URI.create(POINT + "/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .DELETE()
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(201, response.statusCode());
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
            Assertions.fail();
        }
    }

    protected int createEpic(String name, String description) {
        URI url = URI.create(POINT + "/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"" + name + "\",\"description\":\"" + description + "\"}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            return  jsonTask.get("id").getAsInt();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            // Assertions.fail();
        }
        return 0;
    }

    protected int createSubtask(String name, String description, String time, int epicId) {
        URI url = URI.create(POINT + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"" + name +
                        "\",\"description\":\"" + description +
                        "\",\"startTime\":\"" + time +
                        "\",\"epicId\":\"" + epicId +
                        "\",\"duration\":3600}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            JsonElement jsonElement = JsonParser.parseString(response.body());
            JsonObject jsonTask = jsonElement.getAsJsonObject();
            return jsonTask.get("id").getAsInt();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            // Assertions.fail();
        }
        return 0;
    }

    protected void updateSubtaskStatus(int subtaskId, String status) {
        URI url = URI.create(POINT + "/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.ofString("{\"status\":\"" + status + "\"}"))
                .uri(url)
                .build();
        try {
            HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
