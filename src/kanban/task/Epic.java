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
        this.refreshStatus();
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
        this.refreshStatus();
    }

    /**
     * Обновить подзачу
     */
    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        this.refreshStatus();
    }

    /**
     * Удалить подзадачу по ИД
     * @param id
     */
    public void removeSubtask(int id) {
        this.subtasks.remove(id);
        this.refreshStatus();
    }

    /**
     * Обновить статус эпика
     */
    private void refreshStatus() {
        if (this.subtasks.isEmpty() && this.status != Status.NEW) {
            this.status = Status.NEW;
        }
        if (this.subtasks.size() == this.getSubtasksByStatus(Status.NEW).size() && this.status != Status.NEW) {
            this.status = Status.NEW;
        }
        if (this.subtasks.size() == this.getSubtasksByStatus(Status.DONE).size() && this.status != Status.DONE) {
            this.status = Status.DONE;
        }
        if (this.status != Status.IN_PROGRESS) {
            this.status = Status.IN_PROGRESS;
        }
    }

    /**
     * Получить список подзадач в опреденном статусе
     * @param status
     * @return
     */
    private ArrayList<Subtask> getSubtasksByStatus(Status status) {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Map.Entry<Integer, Subtask> subtask : this.subtasks.entrySet()) {
            if (subtask.getValue().status == status) {
                subtasks.add(subtask.getValue());
            }
        }
        return subtasks;
    }
}
