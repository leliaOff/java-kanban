package kanban.manager.history;

import kanban.task.Task;

import java.util.LinkedList;
import java.util.LinkedHashMap;

public class InMemoryHistoryManager implements IHistoryManager {
    private final LinkedHashMap<Integer, Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedHashMap<>();
    }

    /**
     * Добавить запись в историю
     */
    public void add(Task task) {
        this.history.put(task.getId(), task);
    }

    /**
     * Удалить
     * @param id
     */
    public void remove(int id) {
        this.history.remove(id);
    }

    /**
     * История запросов задач
     */
    public LinkedList<Task> getHistory() {
        return new LinkedList<Task>(this.history.values());
    }
}
