package kanban.task;

import kanban.manager.enums.Status;
import kanban.manager.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Task {

    /**
     * ID задачи
     */
    protected int id;

    /**
     * Наименование
     */
    protected String title;


    /**
     * Описание
     */
    protected String description;

    /**
     * Статус
     */
    protected Status status;

    /**
     * Продолжительность задачи
     */
    protected Duration duration;

    /**
     * Время старта выполнения задачи
     */
    protected LocalDateTime startTime;

    protected transient final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String line) {
        String[] data = line.split(";");
        this.id = Integer.parseInt(data[0]);
        this.title = data[2];
        this.status = Status.valueOf(data[3]);
        this.description = data[4];
        if (data[6] != null) {
            try {
                this.startTime = LocalDateTime.parse(data[6], formatter);
            } catch (DateTimeParseException e) {
                System.out.println(e.getMessage());
            }
        }
        if (data[7] != null) {
            this.duration = Duration.ofSeconds(Long.parseLong(data[7]));
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Type getType() {
        if (this instanceof Epic) {
            return Type.EPIC;
        }
        if (this instanceof Subtask) {
            return Type.SUBTASK;
        }
        return Type.TASK;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public LocalDateTime getEndTime() {
        if (this.startTime == null) {
            return null;
        }
        return this.startTime.plus(this.duration);
    }

    /**
     * Проверяет, пересекаются ли задачи
     * @param task  Вторая задача
     * @return  Признак: пересекаются или нет
     */
    public boolean isIntersect(Task task) {
        if (this.getStartTime() == null ||
            this.getEndTime() == null ||
            task.getStartTime() == null ||
            task.getEndTime() == null
        ) {
            return false;
        }
        if (this.getStartTime().equals(task.getStartTime())) {
            return true;
        }
        if (this.getStartTime().isBefore(task.getStartTime())) {
            return this.getEndTime().isAfter(task.getStartTime());
        }
        return task.getEndTime().isAfter(this.getStartTime());
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
                "%d;%s;%s;%s;%s;%s;%s;%d\n",
                this.getId(),
                this.getType(),
                this.getTitle(),
                this.getStatus(),
                this.getDescription(),
                this.getType().equals(Type.SUBTASK) ? ((Subtask) this).getEpicId() : "",
                this.startTime != null ? this.startTime.format(formatter) : "",
                this.duration != null ? this.duration.toSeconds() : 0
        );
    }
}
