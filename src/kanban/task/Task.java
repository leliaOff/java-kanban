package kanban.task;

import java.util.ArrayList;
import java.util.Objects;

public class Task {

    /** ID задачи */
    private int id;

    /** Наименование */
    private String title;


    /** Описание */
    private String description;

    /** Статус */
    private Status status;

    /**
     * construct
     *
     * @param title
     * @param description
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }
}
