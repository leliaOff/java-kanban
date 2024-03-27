package kanban.manager;

import kanban.manager.history.IHistoryManager;
import kanban.manager.history.InMemoryHistoryManager;
import kanban.task.Epic;
import kanban.manager.enums.Status;
import kanban.task.Subtask;
import kanban.task.Task;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements ITaskManager<Integer> {

    /**
     * Последовательность ИД
     */
    protected static int sequenceId = 1;

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
        return this.getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epic.getId())
                .collect(Collectors.toCollection(ArrayList::new));
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
        this.tasks.values().forEach(task -> this.historyManager.remove(task.getId()));
        this.tasks.clear();
        this.prioritizedTasksManager.removeAllTasks();
    }

    /**
     * Удалить все эпики
     */
    @Override
    public void removeAllEpics() {
        this.subtasks.values().forEach(subtask -> this.historyManager.remove(subtask.getId()));
        this.subtasks.clear();
        this.epics.values().forEach(epic -> this.historyManager.remove(epic.getId()));
        this.epics.clear();
        this.prioritizedTasksManager.removeAllEpics();
    }

    /**
     * Удалить все подзадачи
     */
    @Override
    public void removeAllSubtasks() {
        this.subtasks.values().forEach(subtask -> this.historyManager.remove(subtask.getId()));
        this.subtasks.clear();
        this.epics.forEach((key, value) -> {
            value.removeAllSubtasks();
            this.refreshEpic(value);
        });
        this.prioritizedTasksManager.removeAllSubtasks();
    }

    /**
     * Удалить все подзадачи эпика
     */
    @Override
    public void removeAllSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasks = this.getSubtaskByEpic(epic);
        subtasks.forEach(subtask -> {
            this.historyManager.remove(subtask.getId());
            this.subtasks.remove(subtask.getId());
        });
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
    public Optional<Task> getTask(int id) {
        Task task = this.tasks.get(id);
        if (task == null) {
            return Optional.empty();
        }
        this.historyManager.add(task);
        return Optional.of(task);
    }

    /**
     * Поиск эпика по ИД
     *
     * @return Epic|null
     */
    @Override
    public Optional<Epic> getEpic(int id) {
        Epic epic = this.epics.get(id);
        if (epic == null) {
            return Optional.empty();
        }
        this.historyManager.add(epic);
        return Optional.of(epic);
    }

    /**
     * Поиск подзадачи по ИД
     *
     * @return Subtask|null
     */
    @Override
    public Optional<Subtask> getSubtask(int id) {
        Subtask subtask = this.subtasks.get(id);
        if (subtask == null) {
            return Optional.empty();
        }
        this.historyManager.add(subtask);
        return Optional.of(subtask);
    }

    /**
     * Создать задачу
     */
    @Override
    public void addTask(Task task) {
        if (!this.prioritizedTasksManager.getIntersects(task).isEmpty()) {
            return;
        }
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
        if (!this.prioritizedTasksManager.getIntersects(epic).isEmpty()) {
            return;
        }
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
        if (!this.prioritizedTasksManager.getIntersects(subtask).isEmpty()) {
            return;
        }
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
        if (!this.prioritizedTasksManager.getIntersects(task).isEmpty()) {
            return;
        }
        this.tasks.put(task.getId(), task);
    }

    /**
     * Обновить эпик
     */
    @Override
    public void updateEpic(Epic epic) {
        if (!this.prioritizedTasksManager.getIntersects(epic).isEmpty()) {
            return;
        }
        this.epics.put(epic.getId(), epic);
    }

    /**
     * Обновить подзачу
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        if (!this.prioritizedTasksManager.getIntersects(subtask).isEmpty()) {
            return;
        }
        this.subtasks.put(subtask.getId(), subtask);
        this.getEpic(subtask.getEpicId()).ifPresent(this::refreshEpic);
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
        this.getEpic(id).ifPresent(epic -> {
            this.prioritizedTasksManager.remove(this.epics.get(id));
            this.removeAllSubtasksByEpic(epic);
            this.historyManager.remove(id);
            this.epics.remove(id);
        });
    }

    /**
     * Удалить подзадачу по ИД
     */
    @Override
    public void removeSubtask(int id) {
        this.getSubtask(id).flatMap(subtask -> this.getEpic(subtask.getEpicId())).ifPresent(epic -> {
            epic.removeSubtask(id);
            this.historyManager.remove(id);
            this.prioritizedTasksManager.remove(this.subtasks.get(id));
            this.subtasks.remove(id);
            this.refreshEpic(epic);
        });

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
        if (this.getSubtaskByEpic(epic).stream().allMatch(subtask -> subtask.getStatus().equals(Status.NEW))) {
            epic.setStatusNew();
            return;
        }
        if (this.getSubtaskByEpic(epic).stream().allMatch(subtask -> subtask.getStatus().equals(Status.DONE))) {
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
                    return a.getStartTime().isAfter(b.getStartTime()) ? 1 : -1;
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
                    return a.getEndTime().isBefore(b.getEndTime()) ? 1 : -1;
                })
                .map(Task::getEndTime)
                .findFirst();
        epic.setEndTime(endTime.orElse(null));
    }

    /**
     * Увеличить последовательность ИД
     */
    protected static void increaseSequenceId() {
        sequenceId++;
    }

    /**
     * История запросов задач
     */
    public ArrayList<Task> getHistory() {
        return this.historyManager.getHistory();
    }
}
