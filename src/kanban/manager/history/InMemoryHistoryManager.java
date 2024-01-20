package kanban.manager.history;

import java.util.ArrayList;

public class InMemoryHistoryManager implements IHistoryManager<Integer> {
    private final ArrayList<History<Integer>> history;

    private static final int MAX_HISTORY_COUNT = 10;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    /**
     * Добавить запись в историю
     */
    public void add(Class<?> instance, Integer id) {
        History<Integer> item = new History<>(instance, id);
        this.history.add(item);
        if (this.history.size() > MAX_HISTORY_COUNT) {
            this.history.remove(0);
        }
    }

    /**
     * История запросов задач
     */
    public ArrayList<History<Integer>> getHistory() {
        return this.history;
    }
}
