package kanban.manager;

import kanban.manager.exceptions.FileBackedIOException;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import kanban.task.Type;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager implements ITaskManager<Integer> {
    /**
     * Пусть к файлу с информацией о состоянии
     */
    private final String filename;

    public FileBackedTaskManager(String filename) {
        super();
        this.filename = filename;
        this.restore();
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

    /**
     * Востановить состояние задачника
     */
    private void restore() {
        try (FileReader reader = new FileReader(this.filename)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] data = line.split(",");
                if (Type.valueOf(data[1]).equals(Type.EPIC)) {
                    Epic epic = new Epic(line);
                    super.addEpic(epic);
                } else if (Type.valueOf(data[1]).equals(Type.SUBTASK)) {
                    Subtask subtask = new Subtask(line);
                    Epic epic = this.getEpic(subtask.getEpicId());
                    super.addSubtaskByEpic(subtask, epic);
                } else {
                    Task task = new Task(line);
                    super.addTask(task);
                }
            }
            bufferedReader.close();
        } catch (Throwable exception) {
            System.out.println("Во время чтения из файла произошла ошибка");
        }
    }

    /**
     * Сохранить состояние задачника
     */
    private void save() {
        try (FileWriter writer = new FileWriter(this.filename)) {
            // Задачи
            for (Task task : this.getTasks()) {
                writer.write(task.toString());
            }
            // Эпики
            for (Task task : this.getEpics()) {
                writer.write(task.toString());
            }
            // Подзадачи
            for (Task task : this.getSubtasks()) {
                writer.write(task.toString());
            }

        } catch (IOException exception) {
            throw new FileBackedIOException("Во время записи файла произошла ошибка");
        }
    }
}
