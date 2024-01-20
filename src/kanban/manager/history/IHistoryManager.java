package kanban.manager.history;

import java.util.ArrayList;

public interface IHistoryManager<I> {
    /**
     * Добавить запись в историю
     */
    public void add(Class<?> instance, I id);

    /**
     * Вернуть историю
     */
    public ArrayList<History<I>> getHistory();
}
