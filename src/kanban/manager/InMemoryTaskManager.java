package kanban.manager;

import kanban.manager.history.IHistoryManager;
import kanban.task.Epic;
import kanban.task.Status;
import kanban.task.Subtask;
import kanban.task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class InMemoryTaskManager implements ITaskManager<Integer> {

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

    private final IHistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = ManagerFactory.getHistoryManagerInstance();
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
        for (Task task : this.tasks.values()) {
            this.historyManager.remove(task.getId());
        }
        this.tasks.clear();
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
            this.historyManager.remove(subtask.getId());
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
        this.historyManager.remove(id);
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
        this.subtasks.remove(id);
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
