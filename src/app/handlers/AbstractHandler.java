package app.handlers;

import app.adapters.DurationAdapter;
import app.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.nio.charset.Charset;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class AbstractHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected Gson gson;

    protected HttpExchange requestExchange;

    protected String requestMethod;

    protected String requestPath;

    AbstractHandler() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gson = gsonBuilder.create();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        requestExchange = httpExchange;
        requestMethod = httpExchange.getRequestMethod();
        requestPath = httpExchange.getRequestURI().getPath();
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
