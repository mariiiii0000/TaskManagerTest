package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskParserImpl implements TaskParser {

    private String checkDuration(Duration duration) {
        if (duration == null) {
            return "";
        } else {
            return duration.toString();
        }
    }

    private String checkDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        } else {
            return dateTime.toString();
        }
    }

    private Duration checkDurationTo(String value) {
        if (value == null || value.isBlank()) {
            return null;
        } else {
            return Duration.parse(value);
        }
    }

    private LocalDateTime checkDateTimeTo(String value) {
        if (value == null || value.isBlank()) {
            return null;
        } else {
            return LocalDateTime.parse(value);
        }
    }

    private Status parseStatus(String value) {
        if (value == null || value.isBlank()) {
            return Status.NEW;
        } else {
            return Status.valueOf(value);
        }
    }

    @Override
    public String toString(Subtask subtask) {
        return TypesOfTasks.SUBTASK + "," +
                subtask.getId() + "," +
                subtask.getName() + "," +
                subtask.getDescription() + "," +
                subtask.getStatus() + "," +
                subtask.getEpicId() + "," +
                checkDuration(subtask.getDuration()) + "," +
                checkDateTime(subtask.getStartTime());
    }

    @Override
    public String toString(Epic epic) {

        return TypesOfTasks.EPIC + "," +
                epic.getId() + "," +
                epic.getName() + "," +
                epic.getDescription() + "," +
                epic.getStatus() + "," +
                checkDuration(epic.getDuration()) + "," +
                checkDateTime(epic.getStartTime());
    }

    @Override
    public String toString(Task task) {

        return TypesOfTasks.TASK + "," +
                task.getId() + "," +
                task.getName() + "," +
                task.getDescription() + "," +
                task.getStatus() + "," +
                checkDuration(task.getDuration()) + "," +
                checkDateTime(task.getStartTime());
    }

    @Override
    public Task toTask(String string) {
        String[] data = string.split(",");
        long ID  = Long.parseLong(data[1]);
        String name = data[2];
        String description = data[3];
        Status status = parseStatus(data[4]);
        Duration duration = checkDurationTo(data[5]);
        LocalDateTime start = checkDateTimeTo(data[6]);
        return new Task(ID, name, description, status, duration, start);
    }

    @Override
    public Subtask toSubtask(String string) {
        String[] data = string.split(",");
        Status status = parseStatus(data[4]);
        String name = data[2];
        String description = data[3];
        long ID = Long.parseLong(data[1]);
        long epicID = Long.parseLong(data[5]);
        Duration duration = checkDurationTo(data[6]);
        LocalDateTime start = checkDateTimeTo(data[7]);
        return new Subtask(ID, name, description, status, epicID, duration, start);
    }

    @Override
    public Epic toEpic(String string) {
        String[] data = string.split(",");
        long ID = Long.parseLong(data[1]);
        String name = data[2];
        String description = data[3];
        return new Epic(name, description, ID);
    }


    public static List<Long> historyFromString(String value){
        String[] values = value.split(",");
        List<Long> ids = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return ids;
        }
        for (String num : values) {
            ids.add(Long.parseLong(num));
        }
        return ids;
    }
}
