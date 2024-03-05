package kanban.task;

import java.util.ArrayList;
import java.util.Objects;

public class Task {

    /** ID задачи */
    protected int id;

    /** Наименование */
    protected String title;


    /** Описание */
    protected String description;

    /** Статус */
    protected Status status;

    /**
     * construct
     */
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    /**
     * construct
     */
    public Task(String line) {
        String[] data = line.split(";");
        this.id = Integer.parseInt(data[0]);
        this.title = data[2];
        this.status = Status.valueOf(data[3]);
        this.description = data[4];
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Получить ИД задачи
     *
     * @return int ИД задачи
     */
    public int getId() {
        return this.id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Получить статус
     *
     * @return Status Статус
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * Получить Наименование задачи
     *
     * @return String Наименование задачи
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Получить Описание задачи
     *
     * @return String Описание задачи
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Возвращает тип задачи: задача, эпик или подзадача
     * @return  Тип задачи
     */
    public Type getType() {
        if (this instanceof Epic) {
            return Type.EPIC;
        }
        if (this instanceof Subtask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
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

    @Override
    public String toString() {
        return String.format(
                "%d;%s;%s;%s;%s;%s\n",
                this.getId(),
                this.getType(),
                this.getTitle(),
                this.getStatus(),
                this.getDescription(),
                this.getType().equals(Type.SUBTASK) ? ((Subtask) this).getEpicId() : ""
        );
    }
}
