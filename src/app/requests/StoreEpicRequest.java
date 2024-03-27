package app.requests;

import app.services.GsonService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kanban.manager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class StoreEpicRequest extends UpdateEpicRequest {

    public StoreEpicRequest(Optional<JsonElement> requestBody) {
        super(requestBody);
    }

    protected void parse() {
        if (requestBody.isEmpty()) {
            this.validateResult.add("Не передано обязательное поле: title");
            this.validateResult.add("Не передано обязательное поле: description");
            return;
        }
        JsonObject request = requestBody.get().getAsJsonObject();

        if (request.get("title") == null || request.get("title").getAsString().isEmpty()) {
            this.validateResult.add("Не передано обязательное поле: title");
        } else {
            this.title = Optional.of(request.get("title").getAsString());
        }

        if (request.get("description") == null || request.get("description").getAsString().isEmpty()) {
            this.validateResult.add("Не передано обязательное поле: description");
        } else {
            this.description = Optional.of(request.get("description").getAsString());
        }
    }
}
