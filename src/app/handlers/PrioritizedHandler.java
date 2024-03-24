package app.handlers;

import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.task.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PrioritizedHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (isInvalid(httpExchange.getRequestMethod(), httpExchange.getRequestURI().getPath())) {
            writeResponse(httpExchange, "Неверный адрес", 404);
        }
        ArrayList<Task> prioritize = Managers.getInstance().getPrioritizedTasks();
        writeResponse(httpExchange, gson.toJson(prioritize), 200);
    }

    protected boolean isInvalid(String method, String path) {
        return !Objects.equals(method, "GET") ||
                !Objects.equals(path, "/prioritized");
    }
}
