package kanban.manager.exceptions;

public class FileBackedIOException extends RuntimeException {
    public FileBackedIOException(final String message) {
        super(message);
    }
}
