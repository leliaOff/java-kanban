package kanban.history;

import config.tests.App;
import kanban.manager.exceptions.ManagerDeleteException;
import kanban.manager.history.FileBackedHistoryManager;
import kanban.manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class FileBackedHistoryManagerTest extends HistoryManagerTest<FileBackedHistoryManager> {
    @BeforeEach
    public void beforeEach() {
        historyManager = new FileBackedHistoryManager(App.getHistoryFilename());
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.deleteIfExists(Paths.get(App.getHistoryFilename()));
        } catch (IOException exception) {
            throw new ManagerDeleteException(exception.getMessage());
        }
    }
}