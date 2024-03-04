package kanban.manager;

import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import kanban.task.Type;

import java.io.FileWriter;

public class FileBackedTaskManager extends InMemoryTaskManager implements ITaskManager<Integer> {
    /**
     * Пусть к файлу с информацией о состоянии
     */
    private final String filename;

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
    }

    /**
     * Удалить все задачи и эпики
     */
    @Override
    public void removeAll() {
        super.removeAll();
        save();
    }

    /**
     * Удалить все задачи
     */
    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    /**
     * Удалить все эпики
     */
    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    /**
     * Удалить все подзадачи
     */
    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    /**
     * Удалить все подзадачи эпика
     */
    @Override
    public void removeAllSubtasksByEpic(Epic epic) {
        super.removeAllSubtasksByEpic(epic);
        save();
    }

    /**
     * Создать задачу
     */
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    /**
     * Создать эпик
     */
    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    /**
     * Создать подзадачу эпика
     */
    @Override
    public void addSubtaskByEpic(Subtask subtask, Epic epic) {
        super.addSubtaskByEpic(subtask, epic);
        save();
    }

    /**
     * Обновить задачу
     */
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    /**
     * Обновить эпик
     */
    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    /**
     * Обновить подзачу
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    /**
     * Удалить задачу по ИД
     */
    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    /**
     * Удалить эпик по ИД
     */
    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    /**
     * Удалить подзадачу по ИД
     */
    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    private void save() {
        try (FileWriter writer = new FileWriter(this.filename)) {
            writer.write("id,type,name,status,description,epic\n");

            // Задачи
            for (Task task : this.getTasks()) {
                writer.write(getWriterLink(task));
            }

            // Эпики
            for (Task task : this.getEpics()) {
                writer.write(getWriterLink(task));
            }

            // Подзадачи
            for (Task task : this.getSubtasks()) {
                writer.write(getWriterLink(task));
            }

        } catch (Throwable throwable) {
            System.out.println("Во время записи файла произошла ошибка");
        }
    }

    /**
     * Сформировать строку для записи в файл
     *
     * @return  Строка для записи в файл
     */
    private String getWriterLink(Task task)
    {
        return String.format(
                "%d,%s,%s,%s,%s,%s\n",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task.getType().equals(Type.SUBTASK) ? ((Subtask) task).getEpicId() : ""
        );
    }
}
