package kanban.manager;

import config.tests.App;
import kanban.manager.exceptions.ManagerDeleteException;
import kanban.manager.exceptions.ManagerIOException;
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
import java.util.ArrayList;

class FileBackedTaskManagerTest {

    private ArrayList<Task> tasks;
    private ArrayList<Epic> epics;
    private ArrayList<Subtask> subtasks;

    private FileBackedTaskManager taskManager;

    private void createTask(String name, String description) {
        Task task = new Task(name, description);
        taskManager.addTask(task);
        tasks.add(task);
    }

    private Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.addEpic(epic);
        epics.add(epic);
        return epic;
    }

    private void createSubtask(String name, String description, Epic epic) {
        Subtask subtask = new Subtask(name, description);
        taskManager.addSubtaskByEpic(subtask, epic);
        subtasks.add( subtask);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new FileBackedTaskManager(
                App.getTaskFilename(),
                App.getHistoryFilename()
        );
        tasks = new ArrayList<>();
        epics = new ArrayList<>();
        subtasks = new ArrayList<>();
        createTask("Помыть посуду", "Очень тщательно помыть посуду. И дно у тарелок тоже!");
        createTask("Очистить стол от грязных чашек", "Убрать все чашки из под кофе с рабочего стола");
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask("Почистить картошку", "Почистить, помыть и вырезать глазки", epic);
        createSubtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут", epic);
        epic = createEpic("Помыть полы", "Чистые полы - залог крепкой семьи");
        createSubtask("Помыть полы на кухне", "Не забыть подвинуть стол, что бы помыть под ним тоже", epic);
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
    void getTasks() {
        ArrayList<Task> tasks = taskManager.getTasks();
        Assertions.assertEquals(2, tasks.size());
    }

    @Test
    void getEpics() {
        ArrayList<Epic> epics = taskManager.getEpics();
        Assertions.assertEquals(2, epics.size());
    }

    @Test
    void getSubtasks() {
        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
        Assertions.assertEquals(3, subtasks.size());
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