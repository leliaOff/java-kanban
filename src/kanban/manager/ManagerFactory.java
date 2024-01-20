package kanban.manager;

public class ManagerFactory {

    public static ITaskManager<?> getInstance() {
        return getDefault();
    }

    private static ITaskManager<?> getDefault() {
        return new InMemoryITaskManager();
    }
}
