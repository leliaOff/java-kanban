package kanban.manager.exceptions;

public class ManagerSaveException extends ManagerIOException {
    public ManagerSaveException(final String filename) {
        super("Во время записи файла %s произошла ошибка\n", filename);
    }
    public ManagerSaveException() {
        super("Во время записи файла произошла ошибка");
    }
}
