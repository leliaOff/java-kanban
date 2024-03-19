package kanban.manager;

import kanban.task.Epic;
import kanban.manager.enums.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryTaskManagerTest {

    private ArrayList<Task> tasks;
    private ArrayList<Epic> epics;
    private ArrayList<Subtask> subtasks;

    private InMemoryTaskManager taskManager;

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
        subtasks.add(subtask);
    }

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
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
}