package kanban.manager.exceptions;

public class ManagerRestoreException extends ManagerIOException {
    public ManagerRestoreException(final String filename) {
        super("Во время чтения из файла %s произошла ошибка", filename);
    }

    public ManagerRestoreException() {
        super("Во время чтения из файла произошла ошибка");
    }
}
