package kanban.manager.history;

import kanban.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class InMemoryHistoryManager implements IHistoryManager {
    public TaskNode head;
    public TaskNode tail;
    private final ArrayList<TaskNode> history;

    private final LinkedHashMap<Integer, Integer> map;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
        this.map = new LinkedHashMap<>();
        this.head = null;
        this.tail = null;
    }

    /**
     * Добавить запись в историю
     */
    public void add(Task task) {
        if (this.map.containsKey(task.getId())) {
            return;
        }
        this.map.put(task.getId(), this.history.size());
        this.linkLast(new TaskNode(task));
    }

    /**
     * Удалить
     */
    public void remove(int id) {
        if (!this.map.containsKey(id)) {
            return;
        }
        this.removeNode(this.history.get(this.map.get(id)));
        this.map.remove(id);
    }

    /**
     * История запросов задач
     */
    public ArrayList<Task> getHistory() {
        ArrayList<Task> history = new ArrayList<>();
        for (TaskNode node : this.history) {
            history.add(node.value);
        }
        return history;
    }

    /**
     *  Добавить элемент в конец списка
     */
    private void linkLast(TaskNode node) {
        if (this.tail != null) {
            this.tail.next = node;
            node.prev = this.tail;
        }
        if (this.head == null) {
            this.head = node;
        }
        this.tail = node;
        this.history.add(node);
    }

    /**
     *  Удалить элемент по индексу
     */
    private void removeNode(TaskNode node) {
        this.history.remove(node);
        if (this.tail.equals(node)) {
            this.tail = node.prev;
        }
        if (this.head.equals(node)) {
            this.head = node.next;
        }
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
    }
}
