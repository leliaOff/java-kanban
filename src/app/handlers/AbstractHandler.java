package app.handlers;

import app.adapters.DurationAdapter;
import app.adapters.LocalDateTimeAdapter;
import app.services.GsonService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected Gson gson = GsonService.get();

    protected HttpExchange requestExchange;

    protected String requestMethod;

    protected String requestPath;

    protected Optional<JsonElement> requestBody;

    AbstractHandler() {}

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        requestExchange = httpExchange;
        requestMethod = httpExchange.getRequestMethod();
        requestPath = httpExchange.getRequestURI().getPath();

        String request = new String(httpExchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
        if (request.isEmpty()) {
            requestBody = Optional.empty();
        }
        try {
            JsonElement body = JsonParser.parseString(request);
            requestBody = Optional.of(body);
        } catch (Throwable throwable) {
            requestBody = Optional.empty();
        }
    }

    /**
     * Получить параметр запроса
     *
     * @return параметр запроса
     */
    protected Optional<Integer> getIntegerParameter(String patten) {
        Pattern pattern = Pattern.compile(patten);
        Matcher matcher = pattern.matcher(requestPath);
        return matcher.matches() ? Optional.of(Integer.parseInt(matcher.group(1))) : Optional.empty();
    }

    protected void writeResponse(HttpExchange exchange, int responseCode) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        exchange.sendResponseHeaders(responseCode, 0);
        exchange.close();
    }

    protected void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        Headers headers = exchange.getResponseHeaders();
        headers.set("Content-Type", "application/json; charset=" + DEFAULT_CHARSET);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }
}
