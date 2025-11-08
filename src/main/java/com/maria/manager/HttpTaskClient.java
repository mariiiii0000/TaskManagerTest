//package com.maria.manager;
//
//import com.google.gson.Gson;
//import com.maria.model.Task;
//import com.maria.manager.Status;
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import com.google.gson.*;
//import java.time.Duration;
//import java.time.LocalDateTime;
//
//public class HttpTaskClient {
//    public static void main(String[] args) throws IOException, InterruptedException {
//        TaskManager manager = new InMemoryTaskManager();
//        HttpTaskServer server = new HttpTaskServer(manager);
//        server.start();
//
//        HttpClient client = HttpClient.newHttpClient();
//
//        Task newTask = new Task("Test task", "Check POST request", Status.NEW);
//        String json = gson.toJson(newTask);
//
//        URI postUrl = URI.create("http://localhost:8080/tasks/task/");
//        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
//        HttpRequest postRequest = HttpRequest.newBuilder()
//                .uri(postUrl)
//                .POST(body)
//                .build();
//
//        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println("POST: " + postResponse.statusCode());
//        System.out.println("Ответ: " + postResponse.body());
//
//        URI getUrl = URI.create("http://localhost:8080/tasks/task/?id=1");
//        HttpRequest getRequest = HttpRequest.newBuilder()
//                .uri(getUrl)
//                .GET()
//                .build();
//
//        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
//        System.out.println("GET: " + getResponse.statusCode());
//        System.out.println("Ответ: " + getResponse.body());
//
//        // server.stop();
//    }
//}
