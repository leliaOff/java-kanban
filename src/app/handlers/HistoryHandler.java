package app.handlers;

import app.services.Managers;
import com.sun.net.httpserver.HttpExchange;
import kanban.task.Task;

import java.io.IOException;
import java.util.ArrayList;

public class HistoryHandler extends AbstractHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        if (requestMethod.equals("GET") && requestPath.equals("/history")) {
            index();
        }
        writeResponse(requestExchange, "Неверный адрес", 404);
    }

    private void index() throws IOException {
        ArrayList<Task> history = Managers.getInstance().getHistory();
        writeResponse(requestExchange, gson.toJson(history), 200);
    }
}
