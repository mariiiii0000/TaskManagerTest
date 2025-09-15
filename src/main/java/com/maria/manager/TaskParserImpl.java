package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

//GREEN: Хорошее разделение ответственности. Класс занимается только парсингом, что соответствует SRP.
//
//GREEN: Единообразный формат для всех типов задач.
//
//RED: Критичные проблемы с парсингом статуса и обработкой null значений.
// Это может привести к некорректной работе при загрузке данных.
//
//YELLOW: Проблемы с обработкой временных полей. Не учтена возможность null значений.
public class TaskParserImpl implements TaskParser {



    @Override
    public String toString(Subtask subtask) {

        return TypesOfTasks.SUBTASK + "," +
                subtask.getID() + "," +
                subtask.getName() + "," +
                subtask.getDescription() + "," +
                subtask.getStatus() + "," +
                subtask.getEpicID() + "," +
                // RED: Если duration == null, выбросится NPE
                subtask.getDuration() + "," +
                // RED: Если startTime == null, выбросится NPE
                subtask.getStartTime();
    }
    // Аналогично для toString(Epic) и toString(Task)

    @Override
    public String toString(Epic epic) {

        return TypesOfTasks.EPIC + "," +
                epic.getID() + "," +
                epic.getName() + "," +
                epic.getDescription() + "," +
                epic.getStatus() + "," +
                epic.getDuration() + "," +
                epic.getStartTime();
    }

    @Override
    public String toString(Task task) {

        return TypesOfTasks.TASK + "," +
                task.getID() + "," +
                task.getName() + "," +
                task.getDescription() + "," +
                task.getStatus() + "," +
                task.getDuration() + "," +
                task.getStartTime();
    }

    @Override
    public Task toTask(String string) {
        String[] data = string.split(",");
        long ID  = Long.parseLong(data[1]);
        String name = data[2];
        String description = data[3];
        // RED: Критично! Статус всегда устанавливается NEW,
        // игнорируется значение из файла.
        Status status = Status.NEW;
        Duration duration = Duration.parse(data[5]);
        LocalDateTime start = LocalDateTime.parse(data[6]);
        return new Task(ID, name, description, status, duration, start);
    }

    @Override
    public Subtask toSubtask(String string) {
        String[] data = string.split(",");
        // RED: Та же проблема - игнорируется статус из файла
        Status status = Status.NEW;
        String name = data[2];
        String description = data[3];
        long ID = Long.parseLong(data[1]);
        long epicID = Long.parseLong(data[5]);
        Duration duration = Duration.parse(data[6]);
        LocalDateTime start = LocalDateTime.parse(data[7]);
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
}
