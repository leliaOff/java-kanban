package kanban.manager.history;

import kanban.task.Task;

import java.util.ArrayList;

public interface IHistoryManager {
    /**
     * Добавить запись в историю
     */
    public void add(Task task);

    /**
     * Удалить запись из истории
     *
     * @param id
     */
    public void remove(int id);

    /**
     * Вернуть историю
     */
    public ArrayList<Task> getHistory();
}
