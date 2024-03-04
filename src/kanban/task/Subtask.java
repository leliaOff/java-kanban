package kanban.task;

import java.util.ArrayList;

public class Subtask extends Task {

    /** ID Эпика */
    private int epicId;

    /**
     * construct
     */
    public Subtask(String title, String description) {
        super(title, description);
    }

    /**
     * construct
     */
    public Subtask(String line) {
        super(line);
        String[] data = line.split(",");
        this.epicId = Integer.parseInt(data[5]);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
