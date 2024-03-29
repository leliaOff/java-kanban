package kanban.manager.history;

import kanban.manager.exceptions.ManagerIOException;
import kanban.manager.exceptions.ManagerRestoreException;
import kanban.manager.exceptions.ManagerSaveException;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;
import kanban.manager.enums.Type;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileBackedHistoryManager extends InMemoryHistoryManager implements IHistoryManager {
    /**
     * Пусть к файлу с информацией о состоянии
     */
    private final String filename;

    public FileBackedHistoryManager(String filename) {
        super();
        this.filename = filename;
        this.restore();
    }

    /**
     * Добавить запись в историю
     */
    public void add(Task task) {
        super.add(task);
        save();
    }

    /**
     * Удалить
     */
    public void remove(int id) {
        super.remove(id);
        save();
    }

    /**
     * Востановить состояние
     */
    private void restore() {
        if (!Files.exists(Paths.get(this.filename))) {
            return;
        }
        try (FileReader reader = new FileReader(this.filename)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] data = line.split(";");
                if (Type.valueOf(data[1]).equals(Type.EPIC)) {
                    Epic epic = new Epic(line);
                    super.add(epic);
                } else if (Type.valueOf(data[1]).equals(Type.SUBTASK)) {
                    Subtask subtask = new Subtask(line);
                    super.add(subtask);
                } else {
                    Task task = new Task(line);
                    super.add(task);
                }
            }
            bufferedReader.close();
        } catch (Throwable exception) {
            throw new ManagerRestoreException(this.filename);
        }
    }

    /**
     * Сохранить состояние истории
     */
    private void save() {
        try (FileWriter writer = new FileWriter(this.filename)) {
            for (Task task : this.getHistory()) {
                writer.write(task.toString());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException(this.filename);
        }
    }
}
