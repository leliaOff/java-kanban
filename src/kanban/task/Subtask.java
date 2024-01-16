package kanban.task;

public class Subtask extends Task {

    /** ID Эпика */
    private int epicId;

    /**
     * construct
     *
     * @param title
     * @param description
     */
    public Subtask(String title, String description) {
        super(title, description);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
