package kanban.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {

    private static Task task;

    @BeforeEach
    public void beforeEach() {
        task = new Task("Тестовая задача", "Описание тестовой задачи");
        task.setId(1);
    }

    @Test
    void setId() {
        task.setId(666);
        Assertions.assertEquals(666, task.getId());
    }

    @Test
    void getId() {
        Assertions.assertEquals(1, task.getId());
    }

    @Test
    void setStatus() {
        task.setStatus(Status.IN_PROGRESS);
        Assertions.assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void getStatus() {
        Assertions.assertEquals(Status.NEW, task.getStatus());
    }
}