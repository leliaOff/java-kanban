package app.handlers;

import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import kanban.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class HistoryHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (isInvalid(httpExchange.getRequestMethod(), httpExchange.getRequestURI().getPath())) {
            writeResponse(httpExchange, "Неверный адрес", 404);
        }
        ArrayList<Task> history = Managers.getInstance().getHistory();
        writeResponse(httpExchange, gson.toJson(history), 200);
    }

    protected boolean isInvalid(String method, String path) {
        return !Objects.equals(method, "GET") ||
                !Objects.equals(path, "/history");
    }
}
