import app.handlers.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);

            httpServer.createContext("/tasks", new TasksHandler());
            httpServer.createContext("/subtasks", new SubtasksHandler());
            httpServer.createContext("/epics", new EpicsHandler());
            httpServer.createContext("/history", new HistoryHandler());
            httpServer.createContext("/prioritized", new PrioritizedHandler());

            httpServer.start(); // запускаем сервер
            System.out.println("HTTP-сервер запущен на " + PORT + " порту");

        } catch (IOException exception) {
            System.out.println("Ошибка запуска HTTP-сервера: " + exception);
        }

    }
}
