package kanban.manager.exceptions;

public class ManagerDeleteException extends ManagerIOException {
    public ManagerDeleteException(final String error) {
        super("При удалении одного или нескольких файлов произошла ошибка: %s\n", error);
    }
    public ManagerDeleteException() {
        super("При удалении одного или нескольких файлов произошла ошибка\n");
    }
}
