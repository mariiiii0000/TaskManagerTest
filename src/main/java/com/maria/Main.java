package com.maria;

import com.maria.manager.HttpTaskServer;
import com.maria.manager.InMemoryTaskManager;
import com.maria.manager.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
