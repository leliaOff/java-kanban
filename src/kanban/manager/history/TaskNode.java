package kanban.manager.history;

import kanban.task.Task;

public class TaskNode {
    public Task value;

    public TaskNode next;

    public TaskNode prev;

    public TaskNode(Task value) {
        this.value = value;
        this.next = null;
        this.prev = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.value.equals(((TaskNode) o).value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
