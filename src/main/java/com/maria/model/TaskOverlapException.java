package com.maria.model;

// YELLOW: название непрозрачное
public class TaskOverlapException extends RuntimeException {
    public TaskOverlapException(String message) {
        super(message);
    }
}
