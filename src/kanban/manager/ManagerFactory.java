package kanban.manager;

import config.App;
import enums.Mode;
import kanban.manager.history.FileBackedHistoryManager;
import kanban.manager.history.IHistoryManager;
import kanban.manager.history.InMemoryHistoryManager;

public class ManagerFactory {

    public static ITaskManager<?> getManagerInstance() {
        if (App.getMode().equals(Mode.MEMORY)) {
            return new InMemoryTaskManager();
        }
        if (App.getMode().equals(Mode.FILE)) {
            return new FileBackedTaskManager(App.getTaskFilename(), App.getHistoryFilename());
        }
        return getDefaultManager();
    }

    private static ITaskManager<?> getDefaultManager() {
        return new InMemoryTaskManager();
    }

    public static IHistoryManager getHistoryManagerInstance() {
        if (App.getMode().equals(Mode.MEMORY)) {
            return new InMemoryHistoryManager();
        }
        if (App.getMode().equals(Mode.FILE)) {
            return new FileBackedHistoryManager(App.getHistoryFilename());
        }
        return getDefaultHistoryManager();
    }

    private static IHistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
