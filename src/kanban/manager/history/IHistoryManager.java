package kanban.manager.history;

import kanban.task.Task;

import java.util.LinkedList;

public interface IHistoryManager {
    /**
     * Добавить запись в историю
     */
    public void add(Task task);

    /**
     * Вернуть историю
     */
    public LinkedList<Task> getHistory();
}
