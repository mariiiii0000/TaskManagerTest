package com.maria.manager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final Gson gson = new Gson();
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException, InterruptedException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        //Каждая строка создаёт обработчик пути
        httpServer.createContext("/tasks/task", new TaskHandler(manager));
        httpServer.createContext("/tasks/subtask", new TaskHandler(manager));
        httpServer.createContext("/tasks/epic", new TaskHandler(manager));
        httpServer.createContext("/tasks/", new TaskHandler(manager));
        httpServer.createContext("/tasks/history", new TaskHandler(manager));
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер остановлен.");
    }
}
