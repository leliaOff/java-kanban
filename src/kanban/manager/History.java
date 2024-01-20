package kanban.manager;

public class History<T> {
    private final Class<?> instance;

    private final T id;

    public History(Class<?> instance, T id) {
        this.instance = instance;
        this.id = id;
    }

    public  Class<?> getInstance() {
        return this.instance;
    }

    public  T getId() {
        return this.id;
    }
}
