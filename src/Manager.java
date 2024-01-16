import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Manager {

    /** Последовательность ИД */
    private static int sequenceId = 1;

    /** Список задач */
    private HashMap<Integer, Task> tasks;

    /** Список эпиков */
    private HashMap<Integer, Epic> epics;

    /** Список подзадач */
    private HashMap<Integer, Subtask> subtasks;

    public Manager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    /**
     * Получить список всех задач
     * @return ArrayList<Task>
     */
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    /**
     * Получить список всех эпиков
     * @return ArrayList<Epic>
     */
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    /**
     * Получить список всех подзадач
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    /**
     * Получить список всех подзадач эпика
     * @return ArrayList<Subtask>
     */
    public ArrayList<Subtask> getSubtaskByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtasks();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getEpicId() == epic.getId()) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    /**
     * Удалить все задачи и эпики
     */
    public void removeAll() {
        this.removeAllTasks();
        this.removeAllEpics();
        this.removeAllSubtasks();
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
        this.subtasks.clear();
        this.epics.clear();
    }

    /**
     * Удалить все подзадачи
     */
    public void removeAllSubtasks() {
        this.subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            epic.getValue().removeAllSubtasks();
            this.refreshEpicStatus(epic.getValue());
        }
    }

    /**
     * Удалить все подзадачи эпика
     */
    public void removeAllSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        for (Subtask subtask : subtasks) {
            this.subtasks.remove(subtask.getId());
        }
        epic.removeAllSubtasks();
        this.refreshEpicStatus(epic);
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
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    public Subtask getSubtask(int id) {
        return this.subtasks.get(id);
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
        subtask.setEpicId(epic.getId());
        this.subtasks.put(sequenceId, subtask);
        epic.addSubtask(sequenceId);
        this.refreshEpicStatus(epic);
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
        Epic currentEpic = this.epics.get(epic.getId());
        if (epic.getStatus() != currentEpic.getStatus()) {
            epic.setStatus(currentEpic.getStatus());
        }
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Обновить подзачу
     */
    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        this.refreshEpicStatus(this.getEpic(subtask.getEpicId()));
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
        Epic epic = this.getEpic(id);
        this.removeAllSubtasksByEpic(epic);
        this.epics.remove(id);
    }

    /**
     * Удалить подзадачу по ИД
     * @param id
     */
    public void removeSubtask(int id) {
        Subtask subtask = this.getSubtask(id);
        Epic epic = this.getEpic(subtask.getEpicId());
        epic.removeSubtask(id);
        this.subtasks.remove(id);
    }

    /**
     * Обновить статус эпика
     */
    private void refreshEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        if (subtasks.size() == this.getEpicSubtasksByStatus(epic, Status.NEW).size()) {
            epic.setStatus(Status.NEW);
            return;
        }
        if (subtasks.size() == this.getEpicSubtasksByStatus(epic, Status.DONE).size()) {
            epic.setStatus(Status.DONE);
            return;
        }
        epic.setStatus(Status.IN_PROGRESS);
    }

    /**
     * Получить список подзадач эпика в опреденном статусе
     * @param status
     * @return
     */
    private ArrayList<Subtask> getEpicSubtasksByStatus(Epic epic, Status status) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        ArrayList<Subtask> subtasksByStatus = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() == status) {
                subtasksByStatus.add(subtask);
            }
        }
        return subtasksByStatus;
    }

    /**
     * Увеличить последовательность ИД
     */
    private static void increaseSequenceId() {
        sequenceId++;
    }
}
