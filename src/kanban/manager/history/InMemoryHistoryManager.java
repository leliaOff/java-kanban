package kanban.manager.history;

import kanban.task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements IHistoryManager {
    private final LinkedList<Task> history;

    private static final int MAX_HISTORY_COUNT = 10;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    /**
     * Добавить запись в историю
     */
    public void add(Task task) {
        this.history.add(task);
        if (this.history.size() > MAX_HISTORY_COUNT) {
            this.history.remove(0);
        }
    }

    /**
     * История запросов задач
     */
    public LinkedList<Task> getHistory() {
        return this.history;
    }
}
