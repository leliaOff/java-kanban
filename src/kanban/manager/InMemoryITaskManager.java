package kanban.manager;

import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryITaskManager implements ITaskManager<Integer> {

    /**
     * Последовательность ИД
     */
    private static int sequenceId = 1;

    /**
     * Список задач
     */
    private final HashMap<Integer, Task> tasks;

    /**
     * Список эпиков
     */
    private final HashMap<Integer, Epic> epics;

    /**
     * Список подзадач
     */
    private final HashMap<Integer, Subtask> subtasks;

    private final ArrayList<History<Integer>> history;

    private static final int MAX_HISTORY_COUNT = 10;

    public InMemoryITaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.history = new ArrayList<>();
    }

    /**
     * Получить список всех задач
     *
     * @return ArrayList<Task>
     */
    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(this.tasks.values());
    }

    /**
     * Получить список всех эпиков
     *
     * @return ArrayList<Epic>
     */
    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(this.epics.values());
    }

    /**
     * Получить список всех подзадач
     *
     * @return ArrayList<Subtask>
     */
    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(this.subtasks.values());
    }

    /**
     * Получить список всех подзадач эпика
     *
     * @return ArrayList<Subtask>
     */
    @Override
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
    @Override
    public void removeAll() {
        this.removeAllTasks();
        this.removeAllEpics();
        this.removeAllSubtasks();
    }

    /**
     * Удалить все задачи
     */
    @Override
    public void removeAllTasks() {
        this.tasks.clear();
    }

    /**
     * Удалить все эпики
     */
    @Override
    public void removeAllEpics() {
        this.subtasks.clear();
        this.epics.clear();
    }

    /**
     * Удалить все подзадачи
     */
    @Override
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
    @Override
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
    @Override
    public Task getTask(int id) {
        Task task = this.tasks.get(id);
        this.addHistory(task.getClass(), id);
        return task;
    }

    /**
     * Поиск эпика по ИД
     *
     * @return Epic|null
     */
    @Override
    public Epic getEpic(int id) {
        Epic epic = this.epics.get(id);
        this.addHistory(epic.getClass(), id);
        return epic;
    }

    /**
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = this.subtasks.get(id);
        this.addHistory(subtask.getClass(), id);
        return subtask;
    }

    /**
     * Создать задачу
     */
    @Override
    public void addTask(Task task) {
        task.setId(sequenceId);
        this.tasks.put(sequenceId, task);
        increaseSequenceId();
    }

    /**
     * Создать эпик
     */
    @Override
    public void addEpic(Epic epic) {
        epic.setId(sequenceId);
        this.epics.put(sequenceId, epic);
        increaseSequenceId();
    }

    /**
     * Создать подзадачу эпика
     */
    @Override
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
    @Override
    public void updateTask(Task task) {
        this.tasks.put(task.getId(), task);
    }

    /**
     * Обновить эпик
     */
    @Override
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
    @Override
    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        this.refreshEpicStatus(this.getEpic(subtask.getEpicId()));
    }

    /**
     * Удалить задачу по ИД
     */
    @Override
    public void removeTask(int id) {
        this.tasks.remove(id);
    }

    /**
     * Удалить эпик по ИД
     */
    @Override
    public void removeEpic(int id) {
        Epic epic = this.getEpic(id);
        this.removeAllSubtasksByEpic(epic);
        this.epics.remove(id);
    }

    /**
     * Удалить подзадачу по ИД
     */
    @Override
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

    /**
     * Добавить запись в историю
     */
    private void addHistory(Class<?> instance, int id) {
        History<Integer> item = new History<>(instance, id);
        this.history.add(item);
        if (this.history.size() > MAX_HISTORY_COUNT) {
            this.history.remove(0);
        }
    }

    /**
     * История запросов задач
     */
    public ArrayList<History<Integer>> getHistory() {
        return this.history;
    }
}
