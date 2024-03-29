package kanban.manager.history;

import kanban.task.Task;

public class TaskNode {
    private Task value;

    private TaskNode next;

    private TaskNode prev;

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

    public Task getValue() {
        return value;
    }

    public void setValue(Task value) {
        this.value = value;
    }

    public TaskNode getNext() {
        return next;
    }

    public void setNext(TaskNode next) {
        this.next = next;
    }

    public TaskNode getPrev() {
        return prev;
    }

    public void setPrev(TaskNode prev) {
        this.prev = prev;
    }
}
