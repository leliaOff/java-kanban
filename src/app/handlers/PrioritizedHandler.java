package app.handlers;

import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.task.Task;

import java.io.IOException;
import java.util.ArrayList;

public class PrioritizedHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        if (requestMethod.equals("GET") && requestPath.equals("/prioritized")) {
            index();
        }
        writeResponse(requestExchange, "Неверный адрес", 404);
    }

    private void index() throws IOException {
        ArrayList<Task> prioritize = Managers.getInstance().getPrioritizedTasks();
        writeResponse(requestExchange, gson.toJson(prioritize), 200);
    }
}
