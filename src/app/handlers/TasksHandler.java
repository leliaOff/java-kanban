package app.handlers;

import app.requests.StoreTaskRequest;
import app.requests.UpdateTaskRequest;
import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class TasksHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Optional<Integer> id = getIntegerParameter("/tasks/(\\d+)");
        switch (requestMethod) {
            case "GET":
                if (requestPath.equals("/tasks")) {
                    index();
                }
                if (id.isPresent()) {
                    get(id.get());
                }
                break;
            case "POST":
                if (requestPath.equals("/tasks")) {
                    store();
                }
                if (id.isPresent()) {
                    update(id.get());
                }
                break;
            case "DELETE":
                if (id.isPresent()) {
                    delete(id.get());
                }
                break;
        }
        writeResponse(requestExchange, "Неверный адрес", 404);
    }

    /**
     * Список всех задач
     */
    private void index() throws IOException {
        ArrayList<Task> tasks = manager.getTasks();
        writeResponse(requestExchange, gson.toJson(tasks), 200);
    }

    /**
     * Данные о задаче
     *
     * @param id ИД задачи
     */
    private void get(int id) throws IOException {
        Optional<Task> taskOptional = manager.getTask(id);
        if (taskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        writeResponse(requestExchange, gson.toJson(taskOptional.get()), 200);
    }

    /**
     * Создать задачу
     */
    private void store() throws IOException {
        StoreTaskRequest request = new StoreTaskRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }
        Optional<String> title = request.getTitle();
        Optional<String> description = request.getDescription();
        Optional<LocalDateTime> startTime = request.getStartTime();
        Optional<Duration> duration = request.getDuration();

        Task task = startTime.isPresent() && duration.isPresent() ? new Task(title.get(), description.get(), startTime.get(), duration.get())
                : new Task(title.get(), description.get());
        manager.addTask(task);
        if (task.getId() == 0) {
            writeResponse(requestExchange, "Задача пересекается с одной из текущих задач", 406);
            return;
        }
        request.getStatus().ifPresent(task::setStatus);
        manager.updateTask(task);
        writeResponse(requestExchange, gson.toJson(task), 200);
    }

    /**
     * Изменить задачу
     *
     * @param id ИД задачи
     */
    private void update(int id) throws IOException {
        Optional<Task> taskOptional = manager.getTask(id);
        if (taskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }

        UpdateTaskRequest request = new UpdateTaskRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }

        Task task = taskOptional.get();
        request.getTitle().ifPresent(task::setTitle);
        request.getDescription().ifPresent(task::setDescription);
        request.getStartTime().ifPresent(task::setStartTime);
        request.getDuration().ifPresent(task::setDuration);
        request.getStatus().ifPresent(task::setStatus);
        manager.updateTask(task);
        writeResponse(requestExchange, gson.toJson(task), 200);
    }

    /**
     * Удалить задачу
     *
     * @param id ИД задачи
     */
    private void delete(int id) throws IOException {
        Optional<Task> taskOptional = manager.getTask(id);
        if (taskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        manager.removeTask(id);
        writeResponse(requestExchange, 201);
    }
}
