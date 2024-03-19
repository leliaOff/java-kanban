import kanban.manager.ITaskManager;
import kanban.manager.ManagerFactory;
import kanban.task.Epic;
import kanban.manager.enums.Status;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.Optional;

public class Main {

    private static ITaskManager<Integer> taskManager;

    public static void main(String[] args) {
        taskManager = (ITaskManager<Integer>) ManagerFactory.getManagerInstance();

        // Заполнение
        createTasks();
        printTasks();

        // Смена статуса
        taskStatusChange();
        System.out.println("\nПроизошла смена статуса задач:");
        printTasks();

        // Удаление
        removeTask();
        System.out.println("\nПроизошло удаление задачи и эпика:");
        printTasks();

        System.out.println("\nИстория обращения к задачам:");
        System.out.println(taskManager.getHistory());
    }

    /**
     * Заполнения задачника
     */
    private static void createTasks() {
        // Задача №1
        Task task1 = new Task("Помыть посуду", "Очень тщательно помыть посуду. И дно у тарелок тоже!");
        taskManager.addTask(task1);
        // Задача №2
        Task task2 = new Task("Очистить стол от грязных чашек", "Убрать все чашки из под кофе с рабочего стола");
        taskManager.addTask(task2);
        // Эпик №1
        Subtask subtask4 = new Subtask("Почистить картошку", "Почистить, помыть и вырезать глазки");
        Subtask subtask5 = new Subtask("Сварить картошку", "В кипящую соленую воду кинуть помытую и почищенную картошку. Варить 20 минут");
        Epic epic3 = new Epic("Приготовить картошку", "Картошка - это всегда хорошо");
        taskManager.addEpic(epic3);
        taskManager.addSubtaskByEpic(subtask4, epic3);
        taskManager.addSubtaskByEpic(subtask5, epic3);
        // Эпик №2
        Subtask subtask7 = new Subtask("Помыть полы на кухне", "Не забыть подвинуть стол, что бы помыть под ним тоже");
        Epic epic6 = new Epic("Помыть полы", "Чистые полы - залог крепкой семьи");
        taskManager.addEpic(epic6);
        taskManager.addSubtaskByEpic(subtask7, epic6);
    }

    /**
     * Смена статусов задач
     */
    private static void taskStatusChange() {
        Optional<Task> task1 = taskManager.getTask(1);
        task1.ifPresent(task -> {
            task.setStatus(Status.IN_PROGRESS);
            taskManager.updateTask(task);
        });

        Optional<Task> task2 = taskManager.getTask(2);
        task2.ifPresent(task -> {
            task.setStatus(Status.DONE);
            taskManager.updateTask(task);
        });

        Optional<Subtask> subtask4 = taskManager.getSubtask(4);
        subtask4.ifPresent(subtask -> {
            subtask.setStatus(Status.DONE);
            taskManager.updateSubtask(subtask);
        });

        Optional<Subtask> subtask5 = taskManager.getSubtask(5);
        subtask5.ifPresent(subtask -> {
            subtask.setStatus(Status.DONE);
            taskManager.updateSubtask(subtask);
        });

        Optional<Subtask> subtask7 = taskManager.getSubtask(7);
        subtask7.ifPresent(subtask -> {
            subtask.setStatus(Status.IN_PROGRESS);
            taskManager.updateSubtask(subtask);
        });
    }

    /**
     * Удаление задач
     */
    private static void removeTask() {
        Optional<Task> task2 = taskManager.getTask(2);
        task2.ifPresent(task -> taskManager.removeTask(task.getId()));
        Optional<Epic> epic6 = taskManager.getEpic(6);
        epic6.ifPresent(epic -> taskManager.removeEpic(epic.getId()));
    }

    /**
     * Печать содержимого задач
     */
    private static void printTasks() {
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());
    }
}
