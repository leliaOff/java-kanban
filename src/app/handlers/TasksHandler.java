package app.handlers;

import app.requests.StoreTaskRequest;
import app.requests.UpdateTaskRequest;
import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.manager.enums.Status;
import kanban.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TasksHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Optional<Integer> taskId = getIntegerParameter("/tasks/(\\d+)");
        switch (requestMethod) {
            case "GET":
                if (requestPath.equals("/tasks")) {
                    index();
                }
                if (taskId.isPresent()) {
                    get(taskId.get());
                }
                break;
            case "POST":
                if (requestPath.equals("/tasks")) {
                    store();
                }
                if (taskId.isPresent()) {
                    update(taskId.get());
                }
                break;
            case "DELETE":
                if (taskId.isPresent()) {
                    delete(taskId.get());
                }
                break;
        }
        writeResponse(requestExchange, "Неверный адрес", 404);
    }

    /**
     * Список всех задач
     */
    private void index() throws IOException {
        ArrayList<Task> tasks = Managers.getInstance().getTasks();
        writeResponse(requestExchange, gson.toJson(tasks), 200);
    }

    /**
     * Данные о задаче
     *
     * @param id ИД задачи
     */
    private void get(int id) throws IOException {
        Optional<Task> taskOptional = Managers.getInstance().getTask(id);
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
        Managers.getInstance().addTask(task);
        if (task.getId() == 0) {
            writeResponse(requestExchange, "Задача пересекается с одной из текущих задач", 406);
            return;
        }
        request.getStatus().ifPresent(task::setStatus);
        Managers.getInstance().updateTask(task);
        writeResponse(requestExchange, gson.toJson(task), 200);
    }

    /**
     * Изменить задачу
     *
     * @param id ИД задачи
     */
    private void update(int id) throws IOException {
        Optional<Task> taskOptional = Managers.getInstance().getTask(id);
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
        Managers.getInstance().updateTask(task);
        writeResponse(requestExchange, gson.toJson(task), 200);
    }

    /**
     * Удалить задачу
     *
     * @param id ИД задачи
     */
    private void delete(int id) throws IOException {
        Optional<Task> taskOptional = Managers.getInstance().getTask(id);
        if (taskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        Managers.getInstance().removeTask(id);
        writeResponse(requestExchange, 201);
    }
}
