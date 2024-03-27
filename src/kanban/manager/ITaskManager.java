package kanban.manager;

import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.Optional;

public interface ITaskManager<I> {

    /**
     * Получить список всех задач
     *
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getTasks();

    /**
     * Получить список всех задач в порядке приоритета
     *
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getPrioritizedTasks();

    /**
     * Получить список всех эпиков
     *
     * @return ArrayList<Epic>
     */
    public ArrayList<Epic> getEpics();

    /**
     * Получить список всех подзадач
     *
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtasks();

    /**
     * Получить список всех подзадач эпика
     *
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic);

    /**
     * Удалить все задачи и эпики
     */
    public void removeAll();

    /**
     * Удалить все задачи
     */
    public void removeAllTasks();

    /**
     * Удалить все эпики
     */
    public void removeAllEpics();

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks();

    /**
     * Удалить все подзадачи эпика
     */
    public void removeAllSubtasksByEpic(Epic epic);

    /**
     * Поиск задачи по ИД
     *
     * @return Task|null
     */
    public Optional<Task> getTask(int id);

    /**
     * Поиск эпика по ИД
     *
     * @return Epic|null
     */
    public Optional<Epic> getEpic(int id);

    /**
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    public Optional<Subtask> getSubtask(int id);

    /**
     * Создать задачу
     */
    public void addTask(Task task);

    /**
     * Создать эпик
     */
    public void addEpic(Epic epic);

    /**
     * Создать подзадачу эпика
     */
    public void addSubtaskByEpic(Subtask subtask, Epic epic);

    /**
     * Обновить задачу
     */
    public void updateTask(Task task);

    /**
     * Обновить эпик
     */
    public void updateEpic(Epic epic);

    /**
     * Обновить подзачу
     */
    public void updateSubtask(Subtask subtask);

    /**
     * Удалить задачу по ИД
     */
    public void removeTask(int id);

    /**
     * Удалить эпик по ИД
     */
    public void removeEpic(int id);

    /**
     * Удалить подзадачу по ИД
     */
    public void removeSubtask(int id);

    /**
     * История запросов задач
     */
    public ArrayList<Task> getHistory();
}
