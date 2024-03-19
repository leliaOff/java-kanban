package kanban.manager;

import kanban.manager.history.IHistoryManager;
import kanban.manager.history.InMemoryHistoryManager;
import kanban.task.Epic;
import kanban.manager.enums.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements ITaskManager<Integer> {

    /**
     * Последовательность ИД
     */
    private static int sequenceId = 1;

    /**
     * Список задач
     */
    protected final HashMap<Integer, Task> tasks;

    /**
     * Список эпиков
     */
    protected final HashMap<Integer, Epic> epics;

    /**
     * Список подзадач
     */
    protected final HashMap<Integer, Subtask> subtasks;

    protected IHistoryManager historyManager;

    protected PrioritizedTasksManager prioritizedTasksManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
        this.prioritizedTasksManager = new PrioritizedTasksManager();
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
     * Получить список всех задач в порядке приоритета
     *
     * @return ArrayList<Task>
     */
    @Override
    public ArrayList<Task> getPrioritizedTasks() {
        return this.prioritizedTasksManager.getAll();
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
        for (Task task : this.tasks.values()) {
            this.historyManager.remove(task.getId());
        }
        this.tasks.clear();
        this.prioritizedTasksManager.removeAllTasks();
    }

    /**
     * Удалить все эпики
     */
    @Override
    public void removeAllEpics() {
        for (Task subtask : this.subtasks.values()) {
            this.historyManager.remove(subtask.getId());
        }
        this.subtasks.clear();
        for (Task epic : this.epics.values()) {
            this.historyManager.remove(epic.getId());
        }
        this.epics.clear();
        this.prioritizedTasksManager.removeAllEpics();
    }

    /**
     * Удалить все подзадачи
     */
    @Override
    public void removeAllSubtasks() {
        for (Task subtask : this.subtasks.values()) {
            this.historyManager.remove(subtask.getId());
        }
        this.subtasks.clear();
        for (Map.Entry<Integer, Epic> epic : this.epics.entrySet()) {
            epic.getValue().removeAllSubtasks();
            this.refreshEpic(epic.getValue());
        }
        this.prioritizedTasksManager.removeAllSubtasks();
    }

    /**
     * Удалить все подзадачи эпика
     */
    @Override
    public void removeAllSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        for (Subtask subtask : subtasks) {
            this.historyManager.remove(subtask.getId());
            this.subtasks.remove(subtask.getId());
        }
        epic.removeAllSubtasks();
        this.refreshEpic(epic);
        this.prioritizedTasksManager.removeAllSubtasks(epic.getId());
    }

    /**
     * Поиск задачи по ИД
     *
     * @return Task|null
     */
    @Override
    public Task getTask(int id) {
        Task task = this.tasks.get(id);
        if (task == null) {
            return null;
        }
        this.historyManager.add(task);
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
        if (epic == null) {
            return null;
        }
        this.historyManager.add(epic);
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
        if (subtask == null) {
            return null;
        }
        this.historyManager.add(subtask);
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
        this.prioritizedTasksManager.add(task);
    }

    /**
     * Создать эпик
     */
    @Override
    public void addEpic(Epic epic) {
        epic.setId(sequenceId);
        this.epics.put(sequenceId, epic);
        increaseSequenceId();
        this.prioritizedTasksManager.add(epic);
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
        this.refreshEpic(epic);
        increaseSequenceId();
        this.prioritizedTasksManager.add(subtask);
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
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Обновить подзачу
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        this.subtasks.put(subtask.getId(), subtask);
        this.refreshEpic(this.getEpic(subtask.getEpicId()));
    }

    /**
     * Удалить задачу по ИД
     */
    @Override
    public void removeTask(int id) {
        this.historyManager.remove(id);
        this.prioritizedTasksManager.remove(this.tasks.get(id));
        this.tasks.remove(id);
    }

    /**
     * Удалить эпик по ИД
     */
    @Override
    public void removeEpic(int id) {
        Epic epic = this.getEpic(id);
        this.removeAllSubtasksByEpic(epic);
        this.historyManager.remove(id);
        this.prioritizedTasksManager.remove(this.epics.get(id));
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
        this.historyManager.remove(id);
        this.prioritizedTasksManager.remove(this.subtasks.get(id));
        this.subtasks.remove(id);
    }

    /**
     * Обновить вычисляемые поля эпика
     */
    private void refreshEpic(Epic epic) {
        this.refreshEpicStatus(epic);
        this.calculateEpicTime(epic);
    }

    /**
     * Обновить статус эпика
     */
    private void refreshEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        if (subtasks.isEmpty()) {
            epic.setStatusNew();
            return;
        }
        if (subtasks.size() == this.getEpicSubtasksByStatus(epic, Status.NEW).size()) {
            epic.setStatusNew();
            return;
        }
        if (subtasks.size() == this.getEpicSubtasksByStatus(epic, Status.DONE).size()) {
            epic.setStatusDone();
            return;
        }
        epic.setStatusInProgress();
    }

    /**
     * Рассчитать время начала и окончания эпика
     *
     * @param epic Эпик
     */
    private void calculateEpicTime(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        if (subtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Optional<LocalDateTime> startTime = subtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .sorted((a, b) -> {
                    if (a.getStartTime().equals(b.getStartTime())) {
                        return 0;
                    }
                    return a.getStartTime().isBefore(b.getStartTime()) ? 1 : -1;
                })
                .map(Task::getStartTime)
                .findFirst();
        epic.setStartTime(startTime.orElse(null));

        Optional<LocalDateTime> endTime = subtasks.stream()
                .filter(subtask -> subtask.getEndTime() != null)
                .sorted((a, b) -> {
                    if (a.getEndTime().equals(b.getEndTime())) {
                        return 0;
                    }
                    return a.getEndTime().isAfter(b.getEndTime()) ? 1 : -1;
                })
                .map(Task::getEndTime)
                .findFirst();
        epic.setEndTime(endTime.orElse(null));
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
     * История запросов задач
     */
    public ArrayList<Task> getHistory() {
        return this.historyManager.getHistory();
    }
}
