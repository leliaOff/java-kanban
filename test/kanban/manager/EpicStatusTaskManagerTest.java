package kanban.manager;

import kanban.manager.enums.Status;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class EpicStatusTaskManagerTest {

    private ArrayList<Epic> epics;
    private ArrayList<Subtask> subtasks;

    private InMemoryTaskManager taskManager;

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
        epics = new ArrayList<>();
        subtasks = new ArrayList<>();
    }

    @Test
    void allSubtaskIsNew() {
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask("Почистить картошку", "Почистить, помыть и вырезать глазки", epic);
        createSubtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут", epic);
        createSubtask("Подать на стол", "Достать картошку из воды, сверху положить хорошего беларусского сливочного масла и мелконарезанную зелень", epic);
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void allSubtaskIsDone() {
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask("Почистить картошку", "Почистить, помыть и вырезать глазки", epic);
        createSubtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут", epic);
        createSubtask("Подать на стол", "Достать картошку из воды, сверху положить хорошего беларусского сливочного масла и мелконарезанную зелень", epic);
        this.subtasks.forEach(subtask -> {
            subtask.setStatus(Status.DONE);
            this.taskManager.updateSubtask(subtask);
        });
        Assertions.assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void allSubtaskIsNewOrDone() {
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask("Почистить картошку", "Почистить, помыть и вырезать глазки", epic);
        createSubtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут", epic);
        createSubtask("Подать на стол", "Достать картошку из воды, сверху положить хорошего беларусского сливочного масла и мелконарезанную зелень", epic);
        this.subtasks.forEach(subtask -> {
            subtask.setStatus(Status.DONE);
            this.taskManager.updateSubtask(subtask);
        });
        createSubtask("Скушать, разумеется", "И не забыть сказать спасибо!", epic);
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void allSubtaskIsProgress() {
        Epic epic = createEpic("Приготовить картошку", "Картошка - это всегда хорошо");
        createSubtask("Почистить картошку", "Почистить, помыть и вырезать глазки", epic);
        createSubtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут", epic);
        createSubtask("Подать на стол", "Достать картошку из воды, сверху положить хорошего беларусского сливочного масла и мелконарезанную зелень", epic);
        this.subtasks.forEach(subtask -> {
            subtask.setStatus(Status.IN_PROGRESS);
            this.taskManager.updateSubtask(subtask);
        });
        Assertions.assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}