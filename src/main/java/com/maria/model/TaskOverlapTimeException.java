package com.maria.model;

// YELLOW: название непрозрачное++++
public class TaskOverlapTimeException extends RuntimeException {
    public TaskOverlapTimeException(String message) {
        super(message);
    }
}
