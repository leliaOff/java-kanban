package app.requests;

import app.services.GsonService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kanban.manager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class StoreSubtaskRequest extends UpdateSubtaskRequest {

    public StoreSubtaskRequest(Optional<JsonElement> requestBody) {
        super(requestBody);
    }

    protected void parse() {
        if (requestBody.isEmpty()) {
            this.validateResult.add("Не передано обязательное поле: title");
            this.validateResult.add("Не передано обязательное поле: description");
            this.validateResult.add("Не передано обязательное поле: epicId");
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

        if (request.get("epicId") == null || request.get("epicId").getAsInt() <= 0) {
            this.validateResult.add("Не передано обязательное поле: epicId");
        } else {
            this.epicId = Optional.of(request.get("epicId").getAsInt());
        }

        if (request.get("startTime") != null && !request.get("startTime").getAsString().isEmpty()) {
            try {
                this.startTime = Optional.of(GsonService.get().fromJson(request.get("startTime").toString(), LocalDateTime.class));
            } catch (Throwable throwable) {
                this.validateResult.add("Ошибка типа данных: startTime");
            }
        }

        if (request.get("duration") != null && request.get("duration").getAsInt() > 0) {
            try {
                this.duration = Optional.of(GsonService.get().fromJson(request.get("duration").toString(), Duration.class));
            } catch (Throwable throwable) {
                this.validateResult.add("Ошибка типа данных: duration");
            }
        }

        if (request.get("status") != null && !request.get("status").getAsString().isEmpty()) {
            try {
                this.status = Optional.of(Status.valueOf(request.get("status").getAsString()));
            } catch (Throwable throwable) {
                this.validateResult.add("Поле status должно принимать одно из допустимых значений: [NEW, IN_PROGRESS, DONE]");
            }
        }
    }
}
