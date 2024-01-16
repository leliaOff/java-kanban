import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        // Задача №1
        Task task1 = new Task("Помыть посуду", "Очень тщательно помыть посуду. И дно у тарелок тоже!");
        manager.addTask(task1);
        // Задача №2
        Task task2 = new Task("Очистить стол от грязных чашек", "Убрать все чашки из под кофе с рабочего стола");
        manager.addTask(task2);
        // Эпик №1
        Subtask subtask1 = new Subtask("Почистить картошку", "Почистить, помыть и вырезать глазки");
        Subtask subtask2 = new Subtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут");
        Epic epic1 = new Epic("Приготовить картошку", "Картошка - это всегда хорошо");
        manager.addEpic(epic1);
        manager.addSubtaskByEpic(subtask1, epic1);
        manager.addSubtaskByEpic(subtask2, epic1);
        // Эпик №2
        Subtask subtask3 = new Subtask("Помыть полы на кухне", "Не забыть подвинуть стол, что бы помыть под ним тоже");
        Epic epic2 = new Epic("Помыть полы", "Чистые полы - залог крепкой семьи");
        manager.addEpic(epic2);
        manager.addSubtaskByEpic(subtask3, epic2);

        // Печать
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        // Смена статуса
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        task2.setStatus(Status.DONE);
        manager.updateTask(task2);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask2);
        subtask3.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask3);

        System.out.println("\nПроизошла смена статуса задач:");

        // Печать
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());

        // Удаление
        manager.removeTask(task2.getId());
        manager.removeEpic(epic2.getId());

        System.out.println("\nПроизошло удаление задачи и эпика:");

        // Печать
        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubtasks());
    }
}
