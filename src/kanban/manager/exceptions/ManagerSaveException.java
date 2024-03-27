package kanban.manager.exceptions;

public class ManagerSaveException extends ManagerIOException {
    public ManagerSaveException(final String error) {
        super(error);
    }

    public ManagerSaveException() {
        super("Не удалось сохранить задачу");
    }
}
