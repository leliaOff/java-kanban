package kanban.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    /** Список подзадач */
    private HashMap<Integer, Subtask> subtasks;

    /**
     * construct
     *
     * @param title
     * @param description
     */
    public Epic(String title, String description) {
        super(title, description);
    }

    /**
     * Получить все подзадачи
     *
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> subtask : this.subtasks.entrySet()) {
            subtasks.add(subtask.getValue());
        }
        return subtasks;
    }

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks() {
        this.subtasks.clear();
    }

    /**
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    public Subtask getSubtask(int id) {
        return this.subtasks.get(id);
    }

    /**
     * Создать подзадачу
     */
    public void addSubtask(int id, Subtask subtask) {
        this.subtasks.put(id, subtask);
    }

    /**
     * Обновить подзачу
     */
    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
    }

    /**
     * Удалить подзадачу по ИД
     * @param id
     */
    public void removeSubtask(int id) {
        this.subtasks.remove(id);
    }
}
