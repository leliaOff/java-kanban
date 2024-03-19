package kanban.manager;

import config.tests.App;
import kanban.manager.exceptions.ManagerDeleteException;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(
                App.getTaskFilename(),
                App.getHistoryFilename()
        );
        tasks = new ArrayList<>();
        epics = new ArrayList<>();
        subtasks = new ArrayList<>();
        createTask(
                "Помыть посуду",
                "Очень тщательно помыть посуду. И дно у тарелок тоже!",
                LocalDateTime.of(2024, 3, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        createTask(
                "Очистить стол от грязных чашек",
                "Убрать все чашки из под кофе с рабочего стола",
                LocalDateTime.of(2024, 3, 14, 13, 0),
                Duration.ofMinutes(30)
        );
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask(
                "Почистить картошку",
                "Почистить, помыть и вырезать глазки",
                LocalDateTime.of(2024, 3, 14, 14, 0),
                Duration.ofMinutes(20),
                epic
        );
        createSubtask(
                "Сварить картошку",
                "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут",
                LocalDateTime.of(2024, 3, 14, 14, 20),
                Duration.ofMinutes(20),
                epic
        );
        epic = createEpic("Помыть полы", "Чистые полы - залог крепкой семьи");
        createSubtask(
                "Помыть полы на кухне",
                "Не забыть подвинуть стол, что бы помыть под ним тоже",
                LocalDateTime.of(2024, 3, 14, 14, 40),
                Duration.ofMinutes(20),
                epic
        );
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.deleteIfExists(Paths.get(App.getTaskFilename()));
            Files.deleteIfExists(Paths.get(App.getHistoryFilename()));
        } catch (IOException exception) {
            throw new ManagerDeleteException(exception.getMessage());
        }
    }

    @Test
    void filesBackedExists() {
        Assertions.assertTrue(Files.exists(Paths.get(App.getTaskFilename())));
        Assertions.assertFalse(Files.exists(Paths.get(App.getHistoryFilename())));
        taskManager.getTask(tasks.get(0).getId());
        Assertions.assertTrue(Files.exists(Paths.get(App.getHistoryFilename())));
    }

    @Test
    void restoreTasks() {
        FileBackedTaskManager secondTaskManager = new FileBackedTaskManager(
                App.getTaskFilename(),
                App.getHistoryFilename()
        );
        ArrayList<Task> tasks = secondTaskManager.getTasks();
        Assertions.assertEquals(2, tasks.size());
        ArrayList<Epic> epics = secondTaskManager.getEpics();
        Assertions.assertEquals(2, epics.size());
        ArrayList<Subtask> subtasks = secondTaskManager.getSubtasks();
        Assertions.assertEquals(3, subtasks.size());
    }

    @Test
    void restoreHistory() {
        taskManager.getTask(tasks.get(0).getId());
        taskManager.getTask(tasks.get(1).getId());
        taskManager.getSubtask(subtasks.get(0).getId());
        ArrayList<Task> history = taskManager.getHistory();
        FileBackedTaskManager secondTaskManager = new FileBackedTaskManager(
                App.getTaskFilename(),
                App.getHistoryFilename()
        );
        ArrayList<Task> secondHistory = secondTaskManager.getHistory();
        Assertions.assertEquals(history, secondHistory);
    }
}