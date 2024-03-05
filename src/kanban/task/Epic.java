package kanban.task;

import kanban.manager.enums.Status;

import java.util.ArrayList;

public class Epic extends Task {

    /** Список ID подзадач */
    private final ArrayList<Integer> subtaskIds;

    /**
     * construct
     *
     */
    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    /**
     * construct
     */
    public Epic(String line) {
        super(line);
        this.subtaskIds = new ArrayList<>();
    }

    @Override
    public void setStatus(Status status) {

    }

    public void setStatusNew() {
        status = Status.NEW;
    }

    public void setStatusInProgress() {
        status = Status.IN_PROGRESS;
    }

    public void setStatusDone() {
        status = Status.DONE;
    }

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks() {
        this.subtaskIds.clear();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return this.subtaskIds;
    }

    /**
     * Создать подзадачу
     */
    public void addSubtask(int id) {
        this.subtaskIds.add(id);
    }

    /**
     * Удалить подзадачу по ИД
     */
    public void removeSubtask(int id) {
        this.subtaskIds.remove(id);
    }
}
