package kanban.manager.exceptions;

public class ManagerIOException extends RuntimeException {
    public ManagerIOException(final String format, Object... args) {
        super(String.format(format, args));
    }

    public ManagerIOException(final String message) {
        super(message);
    }
}
