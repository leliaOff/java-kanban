package kanban.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    /** Список ID подзадач */
    private ArrayList<Integer> subtaskIds;

    /**
     * construct
     *
     * @param title
     * @param description
     */
    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks() {
        this.subtaskIds.clear();
    }

    /**
     * Создать подзадачу
     */
    public void addSubtask(int id) {
        this.subtaskIds.add(id);
    }

    /**
     * Удалить подзадачу по ИД
     * @param id
     */
    public void removeSubtask(int id) {
        this.subtaskIds.remove(id);
    }
}
