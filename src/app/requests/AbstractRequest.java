package app.requests;

import app.Validator;
import com.google.gson.JsonElement;

import java.util.Optional;

abstract public class AbstractRequest {
    protected Validator validateResult;

    protected final Optional<JsonElement> requestBody;

    public AbstractRequest(Optional<JsonElement> requestBody) {
        this.requestBody = requestBody;
        this.validateResult = new Validator();
    }

    public Validator getValidateResult() {
        return validateResult;
    }

    public boolean isValid() {
        return validateResult.isValid();
    }
}
