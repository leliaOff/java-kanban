package app.handlers;

import app.requests.StoreSubtaskRequest;
import app.requests.StoreTaskRequest;
import app.requests.UpdateSubtaskRequest;
import app.requests.UpdateTaskRequest;
import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.task.Epic;
import kanban.task.Subtask;
import kanban.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtasksHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Optional<Integer> id = getIntegerParameter("/subtasks/(\\d+)");
        switch (requestMethod) {
            case "GET":
                if (requestPath.equals("/subtasks")) {
                    index();
                }
                if (id.isPresent()) {
                    get(id.get());
                }
                break;
            case "POST":
                if (requestPath.equals("/subtasks")) {
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
     * Список всех подзадач
     */
    private void index() throws IOException {
        ArrayList<Subtask> subtasks = Managers.getInstance().getSubtasks();
        writeResponse(requestExchange, gson.toJson(subtasks), 200);
    }

    /**
     * Данные о задаче
     *
     * @param id ИД задачи
     */
    private void get(int id) throws IOException {
        Optional<Subtask> subtaskOptional = Managers.getInstance().getSubtask(id);
        if (subtaskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        writeResponse(requestExchange, gson.toJson(subtaskOptional.get()), 200);
    }

    /**
     * Создать подзадачу
     */
    private void store() throws IOException {
        StoreSubtaskRequest request = new StoreSubtaskRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }
        Optional<String> title = request.getTitle();
        Optional<String> description = request.getDescription();
        Optional<Integer> epicId = request.getEpicId();
        Optional<LocalDateTime> startTime = request.getStartTime();
        Optional<Duration> duration = request.getDuration();

        Optional<Epic> epicOptional = Managers.getInstance().getEpic(epicId.get());
        if (epicOptional.isEmpty()) {
            writeResponse(requestExchange, "Не найден эпик с ID = " + epicId.get(), 404);
            return;
        }

        Subtask subtask = startTime.isPresent() && duration.isPresent() ? new Subtask(title.get(), description.get(), startTime.get(), duration.get())
                : new Subtask(title.get(), description.get());
        Managers.getInstance().addSubtaskByEpic(subtask, epicOptional.get());
        if (subtask.getId() == 0) {
            writeResponse(requestExchange, "Подзадача пересекается с одной из текущих задач", 406);
            return;
        }
        request.getStatus().ifPresent(subtask::setStatus);
        Managers.getInstance().updateSubtask(subtask);
        writeResponse(requestExchange, gson.toJson(subtask), 200);
    }

    /**
     * Изменить подзадачу
     *
     * @param id ИД задачи
     */
    private void update(int id) throws IOException {
        Optional<Subtask> subtaskOptional = Managers.getInstance().getSubtask(id);
        if (subtaskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }

        UpdateSubtaskRequest request = new UpdateSubtaskRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }

        Subtask subtask = subtaskOptional.get();
        request.getTitle().ifPresent(subtask::setTitle);
        request.getDescription().ifPresent(subtask::setDescription);
        request.getStartTime().ifPresent(subtask::setStartTime);
        request.getDuration().ifPresent(subtask::setDuration);
        request.getStatus().ifPresent(subtask::setStatus);
        Managers.getInstance().updateSubtask(subtask);
        writeResponse(requestExchange, gson.toJson(subtask), 200);
    }

    /**
     * Удалить подзадачу
     *
     * @param id ИД задачи
     */
    private void delete(int id) throws IOException {
        Optional<Subtask> subtaskOptional = Managers.getInstance().getSubtask(id);
        if (subtaskOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        Managers.getInstance().removeSubtask(id);
        writeResponse(requestExchange, 201);
    }
}
