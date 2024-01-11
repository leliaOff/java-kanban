import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    /** Последователдьность ИД */
    private static int sequenceId = 1;

    /** Список задач */
    private HashMap<Integer, Task> tasks;

    /** Список эпиков */
    private HashMap<Integer, Epic> epics;

    /**
     * Получить список всех задач
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Map.Entry<Integer, Task> task : this.tasks.entrySet()) {
            tasks.add(task.getValue());
        }
        return tasks;
    }

    /**
     * Получить список всех эпиков
     * @return ArrayList<Epic>
     */
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            epics.add(epic.getValue());
        }
        return epics;
    }

    /**
     * Получить список всех подзадач эпика
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        return epic.getSubtasks();
    }

    /**
     * Получить список всех подзадач
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtask() {
        ArrayList<Subtask> subtasks = new ArrayList<>();
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            subtasks.addAll(this.getSubtaskByEpic(epic.getValue()));
        }
        return subtasks;
    }

    /**
     * Удалить все задачи и эпики
     */
    public void removeAll() {
        this.removeAllTasks();
        this.removeAllEpics();
    }

    /**
     * Удалить все задачи
     */
    public void removeAllTasks() {
        this.tasks.clear();
    }

    /**
     * Удалить все эпики
     */
    public void removeAllEpics() {
        this.epics.clear();
    }

    /**
     * Удалить все подзадачи эпика
     */
    public void removeAllSubtasksByEpic(Epic epic) {
        epic.removeAllSubtasks();
    }

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks() {
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            epic.getValue().removeAllSubtasks();
        }
    }

    /**
     * Поиск задачи по ИД
     *
     * @return Task|null
     */
    public Task getTask(int id) {
        return this.tasks.get(id);
    }

    /**
     * Поиск эпика по ИД
     *
     * @return Epic|null
     */
    public Epic getEpic(int id) {
        return this.epics.get(id);
    }

    /**
     * Поиск подзадачи эпика по ИД
     *
     * @return Subtask|null
     */
    public Subtask getSubtaskByEpic(int id, Epic epic) {
        return epic.getSubtask(id);
    }

    /**
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    public Subtask getSubtask(int id) {
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            Subtask subtask = this.getSubtaskByEpic(id, epic.getValue());
            if (subtask != null) {
                return subtask;
            }
        }
        return null;
    }

    /**
     * Создать задачу
     */
    public void addTask(Task task) {
        task.setId(sequenceId);
        this.tasks.put(sequenceId, task);
        increaseSequenceId();
    }

    /**
     * Создать эпик
     */
    public void addEpic(Epic epic) {
        epic.setId(sequenceId);
        this.epics.put(sequenceId, epic);
        increaseSequenceId();
    }

    /**
     * Создать подзадачу эпика
     */
    public void addSubtaskByEpic(Subtask subtask, Epic epic) {
        subtask.setId(sequenceId);
        subtask.setEpic(epic);
        epic.addSubtask(sequenceId, subtask);
        increaseSequenceId();
    }

    /**
     * Обновить задачу
     */
    public void updateTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    /**
     * Обновить эпик
     */
    public void updateEpic(Epic epic) {
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Обновить подзачу
     */
    public void updateSubtask(Subtask subtask) {
        subtask.getEpic().updateSubtask(subtask);
    }

    /**
     * Удалить задачу по ИД
     * @param id
     */
    public void removeTask(int id) {
        this.tasks.remove(id);
    }

    /**
     * Удалить эпик по ИД
     * @param id
     */
    public void removeEpic(int id) {
        this.epics.remove(id);
    }

    /**
     * Удалить подзадачу эпика по ИД
     * @param id
     */
    public void removeSubtaskByEpic(int id, Epic epic) {
        epic.removeSubtask(id);
    }

    /**
     * Удалить подзадачу по ИД
     * @param id
     */
    public void removeSubtask(int id) {
        Subtask subtask = this.getSubtask(id);
        this.removeSubtaskByEpic(id, subtask.getEpic());
    }

    /**
     * Увеличить последовательность ИД
     */
    private static void increaseSequenceId() {
        sequenceId++;
    }
}
