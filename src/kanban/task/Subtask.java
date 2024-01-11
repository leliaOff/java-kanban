package kanban.task;

public class Subtask extends Task {

    /** Эпик */
    private Epic epic;

    /**
     * construct
     *
     * @param title
     * @param description
     */
    public Subtask(String title, String description) {
        super(title, description);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}
