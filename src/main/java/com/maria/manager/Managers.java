package com.maria.manager;

public class Managers {

    private Managers() {
    }

    public static TaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
    public static TaskManager createFileBackTaskManager() {
        return new FileBackedTaskManager
                ("src/main/resources/manager 2/dataFrom.csv","src/main/resources/manager 2/dataTo");
    }

    public static HistoryManager createHistoryManager() {
        return new InMemoryHistoryManager();
    }
}