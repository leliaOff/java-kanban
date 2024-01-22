package kanban.manager;

import kanban.manager.history.IHistoryManager;
import kanban.manager.history.InMemoryHistoryManager;

public class ManagerFactory {

    public static ITaskManager<?> getManagerInstance() {
        return getDefaultManager();
    }

    private static ITaskManager<?> getDefaultManager() {
        return new InMemoryTaskManager();
    }

    public static IHistoryManager getHistoryManagerInstance() {
        return getDefaultHistoryManager();
    }

    private static IHistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
