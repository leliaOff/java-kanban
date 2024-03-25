package app.requests;

import app.services.GsonService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kanban.manager.enums.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateTaskRequest extends AbstractRequest {
    protected Optional<String> title;
    protected Optional<String> description;
    protected Optional<LocalDateTime> startTime;
    protected Optional<Duration> duration;
    protected Optional<Status> status;

    public UpdateTaskRequest(Optional<JsonElement> requestBody) {
        super(requestBody);
        this.title = Optional.empty();
        this.description = Optional.empty();
        this.startTime = Optional.empty();
        this.duration = Optional.empty();
        this.status = Optional.empty();
        parse();
    }

    protected void parse() {
        if (requestBody.isEmpty()) {
            this.validateResult.add("Запрос пуст");
            return;
        }
        JsonObject request = requestBody.get().getAsJsonObject();

        if (request.get("title") != null && !request.get("title").getAsString().isEmpty()) {
            this.title = Optional.of(request.get("title").getAsString());
        }

        if (request.get("description") != null && !request.get("description").getAsString().isEmpty()) {
            this.description = Optional.of(request.get("description").getAsString());
        }

        if (request.get("startTime") != null && !request.get("startTime").getAsString().isEmpty()) {
            try {
                this.startTime = Optional.of(GsonService.get().fromJson(request.get("startTime").toString(), LocalDateTime.class));
            } catch (Throwable throwable) {
                this.validateResult.add("Ошибка типа данных: startTime");
            }
        }

        if (request.get("duration") != null && request.get("duration").getAsInt() != 0) {
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

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<LocalDateTime> getStartTime() {
        return startTime;
    }

    public Optional<Duration> getDuration() {
        return duration;
    }

    public Optional<Status> getStatus() {
        return status;
    }
}
