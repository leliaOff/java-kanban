package app.handlers;

import app.requests.StoreEpicRequest;
import app.requests.UpdateEpicRequest;
import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.task.Epic;
import kanban.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class EpicsHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Optional<Integer> id = getIntegerParameter("/epics/(\\d+)");
        switch (requestMethod) {
            case "GET":
                if (requestPath.equals("/epics")) {
                    index();
                }
                if (id.isPresent()) {
                    get(id.get());
                }
                break;
            case "POST":
                if (requestPath.equals("/epics")) {
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
     * Список всех эпиков
     */
    private void index() throws IOException {
        ArrayList<Epic> epics = Managers.getInstance().getEpics();
        writeResponse(requestExchange, gson.toJson(epics), 200);
    }

    /**
     * Данные об эпике
     *
     * @param id ИД эпика
     */
    private void get(int id) throws IOException {
        Optional<Epic> epicOptional = Managers.getInstance().getEpic(id);
        if (epicOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        writeResponse(requestExchange, gson.toJson(epicOptional.get()), 200);
    }

    /**
     * Создать эпик
     */
    private void store() throws IOException {
        StoreEpicRequest request = new StoreEpicRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }
        Optional<String> title = request.getTitle();
        Optional<String> description = request.getDescription();

        Epic epic = new Epic(title.get(), description.get());
        Managers.getInstance().addEpic(epic);
        if (epic.getId() == 0) {
            writeResponse(requestExchange, "Эпик пересекается с одной из текущих задач", 406);
            return;
        }
        writeResponse(requestExchange, gson.toJson(epic), 200);
    }

    /**
     * Изменить эпик
     *
     * @param id ИД эпика
     */
    private void update(int id) throws IOException {
        Optional<Epic> epicOptional = Managers.getInstance().getEpic(id);
        if (epicOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }

        UpdateEpicRequest request = new UpdateEpicRequest(requestBody);
        if (!request.isValid()) {
            writeResponse(requestExchange, gson.toJson(request.getValidateResult().getMessages()), 403);
            return;
        }

        Epic epic = epicOptional.get();
        request.getTitle().ifPresent(epic::setTitle);
        request.getDescription().ifPresent(epic::setDescription);
        Managers.getInstance().updateEpic(epic);
        writeResponse(requestExchange, gson.toJson(epic), 200);
    }

    /**
     * Удалить эпик
     *
     * @param id ИД эпика
     */
    private void delete(int id) throws IOException {
        Optional<Epic> epicOptional = Managers.getInstance().getEpic(id);
        if (epicOptional.isEmpty()) {
            writeResponse(requestExchange, "Данные не найдены", 404);
            return;
        }
        Managers.getInstance().removeEpic(id);
        writeResponse(requestExchange, 201);
    }
}
