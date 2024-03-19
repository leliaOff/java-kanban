package kanban.manager;

import kanban.manager.enums.Type;
import kanban.task.Subtask;
import kanban.task.Task;
import java.util.*;
import java.util.stream.Collectors;

public class PrioritizedTasksManager {
    protected TreeSet<Task> prioritizedTasks;

    public PrioritizedTasksManager() {
        Comparator<Task> prioritizedTasksComparator = new Comparator<>() {
            @Override
            public int compare(Task a, Task b) {
                if (a.getStartTime().equals(b.getStartTime())) {
                    return 0;
                }
                return a.getStartTime().isBefore(b.getStartTime()) ? 1 : -1;
            }
        };
        this.prioritizedTasks = new TreeSet<>(prioritizedTasksComparator);
    }

    /**
     * Получить список всех задач в порядке приоритета
     *
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getAll() {
        return new ArrayList<>(this.prioritizedTasks);
    }

    /**
     * Получить список задач, которые пересекаются с переданной в метод
     * @param task  Задача
     * @return  Список задач, которые пересекаются с переданной в метод
     */
    public ArrayList<Task> getIntersects(Task task)
    {
        return this.prioritizedTasks.stream()
                .filter(t -> t.isIntersect(task))
                .filter(t -> t.getId() != task.getId())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Добавить задачу в список
     *
     * @param task  Задача
     */
    public void add(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        this.prioritizedTasks.add(task);
    }

    /**
     * Удалить задачу из списка
     *
     * @param task  Задача
     */
    public void remove(Task task) {
        this.prioritizedTasks.remove(task);
    }


    /**
     * Удалить несколько задач из списка
     */
    public void remove(Set<Task> removed) {
        this.prioritizedTasks.removeAll(removed);
    }

    /**
     * Удалить все задачи из списка
     */
    public void removeAllTasks() {
        this.removeByType(Type.TASK);
    }

    /**
     * Удалить все эпики из списка
     */
    public void removeAllEpics() {
        this.removeByType(Type.EPIC);
    }

    /**
     * Удалить все подзадачи из списка
     */
    public void removeAllSubtasks() {
        this.removeByType(Type.SUBTASK);
    }

    /**
     * Удалить все подзадачи эпика из списка
     */
    public void removeAllSubtasks(int epicId) {
        this.remove(this.prioritizedTasks.stream()
                .filter(task -> task.getType().equals(Type.SUBTASK))
                .map(task -> (Subtask)task)
                .filter(task -> task.getEpicId() ==epicId)
                .collect(Collectors.toSet()));
    }

    /**
     * Удалить все задачи одного типа из списка
     *
     * @param type  Тип
     */
    protected void removeByType(Type type) {
        this.remove(this.prioritizedTasks.stream()
                .filter(task -> task.getType().equals(type))
                .collect(Collectors.toSet()));
    }
}
