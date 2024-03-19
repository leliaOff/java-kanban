package kanban.history;

import kanban.manager.history.IHistoryManager;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;

abstract class HistoryManagerTest<T extends IHistoryManager> {
    protected T historyManager;

    @Test
    void addTask() {
        Task a = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(1);
        historyManager.add(a);
        Assertions.assertEquals(1, historyManager.getHistory().size());
        Task b = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(2);
        historyManager.add(b);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void removeTask() {
        Task a = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(1);
        historyManager.add(a);
        Task b = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        b.setId(2);
        historyManager.add(b);
        Task c = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        c.setId(3);
        historyManager.add(c);
        Task d = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        d.setId(4);
        historyManager.add(d);
        Task e = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        e.setId(5);
        historyManager.add(e);
        Assertions.assertEquals(5, historyManager.getHistory().size());
        historyManager.remove(3);
        Assertions.assertEquals(4, historyManager.getHistory().size());
        historyManager.remove(1);
        Assertions.assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(5);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }

    @Test
    void getNullHistory() {
        Assertions.assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void addRepeatedTask() {
        Task a = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(1);
        historyManager.add(a);
        Task b = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(2);
        historyManager.add(b);
        Task c = new Task(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        a.setId(1);
        historyManager.add(c);
        Assertions.assertEquals(2, historyManager.getHistory().size());
    }
}