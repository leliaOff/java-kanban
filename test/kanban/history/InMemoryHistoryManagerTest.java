package kanban.history;

import kanban.manager.history.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;

class InMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager> {
    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }
}