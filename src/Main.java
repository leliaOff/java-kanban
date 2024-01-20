import kanban.manager.InMemoryITaskManager;
import kanban.manager.ManagerFactory;
import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

public class Main {

    public static void main(String[] args) {
        InMemoryITaskManager inMemoryTaskManager = (InMemoryITaskManager) ManagerFactory.getInstance();

        // Задача №1
        Task task1 = new Task("Помыть посуду", "Очень тщательно помыть посуду. И дно у тарелок тоже!");
        inMemoryTaskManager.addTask(task1);
        // Задача №2
        Task task2 = new Task("Очистить стол от грязных чашек", "Убрать все чашки из под кофе с рабочего стола");
        inMemoryTaskManager.addTask(task2);
        // Эпик №1
        Subtask subtask1 = new Subtask("Почистить картошку", "Почистить, помыть и вырезать глазки");
        Subtask subtask2 = new Subtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут");
        Epic epic1 = new Epic("Приготовить картошку", "Картошка - это всегда хорошо");
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubtaskByEpic(subtask1, epic1);
        inMemoryTaskManager.addSubtaskByEpic(subtask2, epic1);
        // Эпик №2
        Subtask subtask3 = new Subtask("Помыть полы на кухне", "Не забыть подвинуть стол, что бы помыть под ним тоже");
        Epic epic2 = new Epic("Помыть полы", "Чистые полы - залог крепкой семьи");
        inMemoryTaskManager.addEpic(epic2);
        inMemoryTaskManager.addSubtaskByEpic(subtask3, epic2);

        // Печать
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Смена статуса
        task1.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        inMemoryTaskManager.updateTask(task2);

        subtask1.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(subtask2);
        subtask3.setStatus(Status.IN_PROGRESS);
        inMemoryTaskManager.updateSubtask(subtask3);

        System.out.println("\nПроизошла смена статуса задач:");

        // Печать
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());

        // Удаление
        inMemoryTaskManager.removeTask(task2.getId());
        inMemoryTaskManager.removeEpic(epic2.getId());

        System.out.println("\nПроизошло удаление задачи и эпика:");

        // Печать
        System.out.println(inMemoryTaskManager.getTasks());
        System.out.println(inMemoryTaskManager.getEpics());
        System.out.println(inMemoryTaskManager.getSubtasks());
    }
}
