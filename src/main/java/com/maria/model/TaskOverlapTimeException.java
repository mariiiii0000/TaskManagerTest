package com.maria.model;

public class TaskOverlapTimeException extends RuntimeException {
    public TaskOverlapTimeException(String message) {
        super(message);
    }
}
