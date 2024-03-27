package app.requests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Optional;

public class UpdateEpicRequest extends AbstractRequest {
    protected Optional<String> title;
    protected Optional<String> description;

    public UpdateEpicRequest(Optional<JsonElement> requestBody) {
        super(requestBody);
        this.title = Optional.empty();
        this.description = Optional.empty();
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
    }

    public Optional<String> getTitle() {
        return title;
    }

    public Optional<String> getDescription() {
        return description;
    }
}
