package app.services;

import app.adapters.DurationAdapter;
import app.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;

public final class GsonService {
    private GsonService() { }

    private static final Gson gson = build();

    private static Gson build() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        return gsonBuilder.create();
    }

    public static Gson get() {
        return gson;
    }
}
