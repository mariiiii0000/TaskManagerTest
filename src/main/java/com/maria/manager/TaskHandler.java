package com.maria.manager;

import com.google.gson.Gson;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class TaskHandler implements HttpHandler {
    private final Gson gson = new Gson();
    TaskManager manager;

    public TaskHandler(TaskManager manager){
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod(); // GET, POST или DELETE
        String path = exchange.getRequestURI().getPath(); // /tasks/task
        String query = exchange.getRequestURI().getQuery(); // id=1
        String response = "";

        try {
            Endpoint endpoint = getEndpoint(path, method);

            if (path.contains("/task")) {
                response = handleTask(endpoint, query, exchange);
            } else if (path.contains("/subtask")) {
                response = handleSubtask(endpoint, query, exchange);
            } else if (path.contains("/epic")) {
                response = handleEpic(endpoint, query, exchange);
            } else if (path.contains("/history")) {
                response = gson.toJson(manager.getHistory());
            } else {
                response = "Unknown endpoint.";
            }
            if (response == null) {
                response = "No response produced.";
            }

            exchange.sendResponseHeaders(200, response.getBytes().length);
        } catch (Exception e) {
        String errorMessage = "Exception: " + e.getMessage();
        e.printStackTrace();
        exchange.sendResponseHeaders(500, errorMessage.getBytes().length);
        exchange.getResponseBody().write(errorMessage.getBytes());
        exchange.close();
        return;
    }
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();

    }

    private String handleTask(Endpoint endpoint, String query, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    return gson.toJson(manager.getTaskByID(id));
                }
                break;

            case POST:
                String body = new String(exchange.getRequestBody().readAllBytes());
                Task task = gson.fromJson(body, Task.class);

                boolean isExist = false;
                for (Task t : manager.getTasks()) {
                    if (t.getId() == task.getId()) {
                        isExist = true;
                        break;
                    }
                }

                if (isExist) {
                    manager.updateTask(task);
                    return "Task updated.";
                } else {
                    manager.createTask(task);
                    return "Task created.";
                }
            case DELETE:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    manager.removeTasksByID(id);
                    return "Task with id: " + id + " deleted.";
                }
                break;

            default:
                return "Unknown method.";
        }
        return query;
    }

    private String handleSubtask(Endpoint endpoint, String query, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    return gson.toJson(manager.getSubtaskByID(id));
                } else {
                    return gson.toJson(manager.getSubtasks());
                }
            case POST:
                String body = new String(exchange.getRequestBody().readAllBytes());
                Subtask subtask = gson.fromJson(body, Subtask.class);

                boolean isExist = false;
                for (Subtask st : manager.getSubtasks()) {
                    if (st.getId() == subtask.getId()) {
                        isExist = true;
                        break;
                    }
                }

                if (isExist) {
                    manager.updateSubtask(subtask);
                    return "Subtask updated.";
                } else {
                    manager.createSubtask(subtask);
                    return "Subtask created";
                }
            case DELETE:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    manager.removeSubtaskByID(id);
                    return "Subtask was deleted.";
                }
            default:
                return "Wrong method.";
        }
    }


    private String handleEpic(Endpoint endpoint, String query, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    return gson.toJson(manager.getEpicByID(id));
                } else {
                    return gson.toJson(manager.getEpics());
                }
            case POST:
                String body = new String(exchange.getRequestBody().readAllBytes());
                Epic epic = gson.fromJson(body, Epic.class);

                boolean isExist = false;
                for (Epic ep : manager.getEpics()) {
                    if (ep.getId() == epic.getId()) {
                        isExist = true;
                        break;
                    }
                }

                if (isExist) {
                    manager.updateEpic(epic);
                    return "Epic updated.";
                } else {
                    manager.createEpic(epic);
                    return "Epic created";
                }
            case DELETE:
                if (query != null && query.startsWith("id=")) {
                    long id = Long.parseLong(query.substring(3));
                    manager.removeEpicByID(id);
                    return "Epic was deleted.";
                }
            default:
                return "Wrong method.";
        }
    }

    private Endpoint getEndpoint(String path, String method) {
        switch (method) {
            case "GET": return Endpoint.GET;
            case "POST": return Endpoint.POST;
            case "DELETE": return Endpoint.DELETE;
            default: return null;
        }
    }
    enum Endpoint {GET, POST, DELETE}
}



