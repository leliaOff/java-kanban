package app.services;

import kanban.manager.ITaskManager;
import kanban.manager.ManagerFactory;

public final class Managers {
    private Managers() {}

    private static final ITaskManager<Integer> taskManager = (ITaskManager<Integer>) ManagerFactory.getManagerInstance();

    public static ITaskManager<Integer> getInstance() {
        return taskManager;
    }
}
