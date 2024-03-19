package kanban.manager;

import kanban.manager.enums.Status;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

abstract class TaskManagerTest<T extends ITaskManager<Integer>> {

    protected ArrayList<Task> tasks;
    protected ArrayList<Epic> epics;
    protected ArrayList<Subtask> subtasks;

    protected T taskManager;

    protected void createTask(String name, String description, LocalDateTime time, Duration duration) {
        Task task = new Task(name, description, time, duration);
        taskManager.addTask(task);
        tasks.add(task);
    }

    protected Epic createEpic(String name, String description) {
        Epic epic = new Epic(name, description);
        taskManager.addEpic(epic);
        epics.add(epic);
        return epic;
    }

    protected void createSubtask(String name, String description, LocalDateTime time, Duration duration, Epic epic) {
        Subtask subtask = new Subtask(name, description, time, duration);
        taskManager.addSubtaskByEpic(subtask, epic);
        subtasks.add(subtask);
    }

    @BeforeEach
    abstract public void beforeEach();

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
    void getSubtaskByEpic() {
        Epic epic = taskManager.getEpics().get(0);
        ArrayList<Subtask> subtasks = taskManager.getSubtaskByEpic(epic);
        Assertions.assertEquals(2, subtasks.size());
    }

    @Test
    void removeAll() {
        taskManager.removeAll();
        Assertions.assertEquals(0, taskManager.getTasks().size());
        Assertions.assertEquals(0, taskManager.getEpics().size());
        Assertions.assertEquals(0, taskManager.getSubtasks().size());
    }

    @Test
    void removeAllSubtasksByEpic() {
        Epic epic = taskManager.getEpics().get(0);
        taskManager.removeAllSubtasksByEpic(epic);
        ArrayList<Subtask> subtasks = taskManager.getSubtaskByEpic(epic);
        Assertions.assertEquals(0, subtasks.size());
    }

    @Test
    void getTask() {
        Task currentTask = tasks.get(0);
        Task inMemoryTask = taskManager.getTask(currentTask.getId());
        Assertions.assertEquals(currentTask, inMemoryTask);
    }

    @Test
    void getEpic() {
        Epic currentEpic = epics.get(0);
        Epic inMemoryEpic = taskManager.getEpic(currentEpic.getId());
        Assertions.assertEquals(currentEpic, inMemoryEpic);
    }

    @Test
    void getSubtask() {
        Subtask currentSubtask = subtasks.get(0);
        Subtask inMemorySubtask = taskManager.getSubtask(currentSubtask.getId());
        Assertions.assertEquals(currentSubtask, inMemorySubtask);
    }

    @Test
    void addTask() {
        Task newTask = new Task("Тестовая задача", "Описание тестовой задачи");
        taskManager.addTask(newTask);
        Task inMemoryTask = taskManager.getTask(newTask.getId());
        Assertions.assertEquals(newTask.getId(), inMemoryTask.getId());
        Assertions.assertEquals(newTask, inMemoryTask);
        Assertions.assertEquals(Status.NEW, inMemoryTask.getStatus(), "Статус созданной задачи должен быть: новая");
    }

    @Test
    void addEpic() {
        Epic newEpic = new Epic("Тестовый эпик", "Описание тестового эпика");
        taskManager.addEpic(newEpic);
        Epic inMemoryEpic = taskManager.getEpic(newEpic.getId());
        Assertions.assertEquals(newEpic.getId(), inMemoryEpic.getId());
        Assertions.assertEquals(newEpic, inMemoryEpic);
        Assertions.assertEquals(Status.NEW, inMemoryEpic.getStatus(), "Статус созданного эпика должен быть: новый");
    }

    @Test
    void addSubtaskByEpic() {
        Epic epic = epics.get(0);
        Subtask newSubtask = new Subtask("Тестовая подзадача", "Описание тестовой задачи");
        taskManager.addSubtaskByEpic(newSubtask, epic);
        Subtask inMemorySubtask = taskManager.getSubtask(newSubtask.getId());
        Assertions.assertEquals(newSubtask.getId(), inMemorySubtask.getId());
        Assertions.assertEquals(newSubtask, inMemorySubtask);
        Assertions.assertEquals(Status.NEW, inMemorySubtask.getStatus(), "Статус созданной подзадачи должен быть: новая");
    }

    @Test
    void updateTaskStatus() {
        Task currentTask = tasks.get(0);
        Task inMemoryTask = taskManager.getTask(currentTask.getId());
        inMemoryTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(inMemoryTask);
        Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getTask(currentTask.getId()).getStatus());
    }

    @Test
    void updateEpicStatus() {
        Epic currentEpic = epics.get(0);
        Epic inMemoryEpic = taskManager.getEpic(currentEpic.getId());
        inMemoryEpic.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(inMemoryEpic);
        Assertions.assertEquals(Status.NEW, taskManager.getEpic(currentEpic.getId()).getStatus(), "Нельзя менять статус эпика вручную");

        ArrayList<Integer> currentEpicSubtaskIds = currentEpic.getSubtaskIds();
        for (int i = 0; i < currentEpicSubtaskIds.size(); i++) {
            Subtask subtask = taskManager.getSubtask(currentEpicSubtaskIds.get(i));
            subtask.setStatus(Status.DONE);
            taskManager.updateSubtask(subtask);
            if (i == currentEpicSubtaskIds.size() - 1) {
                Assertions.assertEquals(Status.DONE, taskManager.getEpic(currentEpic.getId()).getStatus(), "Статус эпика должен быть: выполнено");
            } else {
                Assertions.assertEquals(Status.IN_PROGRESS, taskManager.getEpic(currentEpic.getId()).getStatus(), "Статус эпика должен быть: в работе");
            }
        }
    }

    @Test
    void updateSubtaskStatus() {
        Subtask currentSubtask = subtasks.get(0);
        Subtask inMemorySubtask = taskManager.getSubtask(currentSubtask.getId());
        inMemorySubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(inMemorySubtask);
        Assertions.assertEquals(Status.DONE, taskManager.getSubtask(currentSubtask.getId()).getStatus());
    }

    @Test
    void removeTask() {
        Task currentTask = tasks.get(0);
        int id = currentTask.getId();
        Assertions.assertNotNull(taskManager.getTask(id), "Результат не может быть null");
        taskManager.removeTask(id);
        Assertions.assertNull(taskManager.getTask(id), "Результат должен быть null, так как задача была удалена");
    }

    @Test
    void getHistory() {
        taskManager.getTask(tasks.get(0).getId());
        taskManager.getTask(tasks.get(1).getId());
        taskManager.getSubtask(subtasks.get(0).getId());
        ArrayList<Task> history = taskManager.getHistory();
        Assertions.assertEquals(3, history.size());
        taskManager.getTask(tasks.get(0).getId());
        taskManager.getTask(tasks.get(1).getId());

        history = taskManager.getHistory();
        Assertions.assertEquals(3, history.size());
        taskManager.removeTask(tasks.get(0).getId());
        taskManager.removeTask(tasks.get(1).getId());

        history = taskManager.getHistory();
        Assertions.assertEquals(1, history.size());
    }

    @Test
    void addTaskWithValidInterval() {
        Task inMemoryTask;
        Task a = new Task(
                "Задача №1",
                "Описание тестовой задачи №1",
                LocalDateTime.of(2024, 4, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(a);
        inMemoryTask = taskManager.getTask(a.getId());
        Assertions.assertEquals(a.getId(), inMemoryTask.getId());
        Assertions.assertEquals(a, inMemoryTask);

        Task b = new Task(
                "Задача №2",
                "Описание тестовой задачи №2",
                LocalDateTime.of(2024, 4, 14, 13, 0),
                Duration.ofMinutes(30)
        );
        taskManager.addTask(b);
        inMemoryTask = taskManager.getTask(b.getId());
        Assertions.assertEquals(b.getId(), inMemoryTask.getId());
        Assertions.assertEquals(b, inMemoryTask);

        Task c = new Task(
                "Задача №3",
                "Описание тестовой задачи №3",
                LocalDateTime.of(2024, 4, 14, 13, 30),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(c);
        inMemoryTask = taskManager.getTask(c.getId());
        Assertions.assertEquals(c.getId(), inMemoryTask.getId());
        Assertions.assertEquals(c, inMemoryTask);
    }

    @Test
    void addTaskWithInvalidInterval() {
        Task inMemoryTask;
        Task a = new Task(
                "Задача №1",
                "Описание тестовой задачи №1",
                LocalDateTime.of(2024, 4, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(a);
        inMemoryTask = taskManager.getTask(a.getId());
        Assertions.assertEquals(a.getId(), inMemoryTask.getId());
        Assertions.assertEquals(a, inMemoryTask);

        Task b = new Task(
                "Задача №2",
                "Описание тестовой задачи №2",
                LocalDateTime.of(2024, 4, 14, 13, 0),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(b);
        inMemoryTask = taskManager.getTask(b.getId());
        Assertions.assertEquals(b.getId(), inMemoryTask.getId());
        Assertions.assertEquals(b, inMemoryTask);

        Task c = new Task(
                "Задача №3",
                "Описание тестовой задачи №3",
                LocalDateTime.of(2024, 4, 14, 13, 30),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(c);
        inMemoryTask = taskManager.getTask(c.getId());
        Assertions.assertNull(inMemoryTask);
    }

    @Test
    void calculateEndTime() {
        Task inMemoryTask;
        Task a = new Task(
                "Задача №1",
                "Описание тестовой задачи №1",
                LocalDateTime.of(2024, 4, 14, 12, 0),
                Duration.ofMinutes(60)
        );
        taskManager.addTask(a);
        inMemoryTask = taskManager.getTask(a.getId());
        Assertions.assertEquals(LocalDateTime.of(2024, 4, 14, 13, 0), inMemoryTask.getEndTime());
        inMemoryTask.setDuration(Duration.ofMinutes(35));
        Assertions.assertEquals(LocalDateTime.of(2024, 4, 14, 12, 35), inMemoryTask.getEndTime());
    }

    @Test
    void calculateEpicDuration() {
        Epic epic = createEpic("Тестовый эпик", "Тест");
        createSubtask(
                "Тестовая подзадача №1",
                "Тест",
                LocalDateTime.of(2024, 3, 15, 9, 0),
                Duration.ofMinutes(5),
                epic
        );
        createSubtask(
                "Тестовая подзадача №2",
                "Тест",
                LocalDateTime.of(2024, 3, 15, 9, 5),
                Duration.ofMinutes(10),
                epic
        );
        createSubtask(
                "Тестовая подзадача №3",
                "Тест",
                LocalDateTime.of(2024, 3, 15, 9, 10),
                Duration.ofMinutes(60),
                epic
        );
        createSubtask(
                "Тестовая подзадача №4",
                "Тест",
                LocalDateTime.of(2024, 3, 15, 9, 15),
                Duration.ofMinutes(15),
                epic
        );
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 15, 9, 0), epic.getStartTime());
        Assertions.assertEquals(LocalDateTime.of(2024, 3, 15, 9, 30), epic.getEndTime());
        Assertions.assertEquals(Duration.ofMinutes(30), epic.getDuration());
    }
}