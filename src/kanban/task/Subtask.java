package kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Subtask extends Task {

    /** ID Эпика */
    private int epicId;

    public Subtask(String title, String description) {
        super(title, description);
    }

    public Subtask(String title, String description, LocalDateTime startTime, Duration duration) {
        super(title, description, startTime, duration);
    }

    public Subtask(String line) {
        super(line);
        String[] data = line.split(";");
        this.epicId = Integer.parseInt(data[5]);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
