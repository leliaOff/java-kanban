package kanban.manager;

import kanban.task.Epic;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
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
}