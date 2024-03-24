package app.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null || duration.toSeconds() == 0) {
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(duration.toSeconds());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        return Duration.ofSeconds(jsonReader.nextInt());
    }
}