package com.maria.model;

import com.maria.manager.Status;
//RED: Неиспользуемый импорт++++

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    // YELLOW: Имя переменной 'ID' нарушает конвенцию Java.+++++
    // Принято использовать lowerCamelCase: 'id'.+++++
    protected long id;
    protected Status status;
    // YELLOW: Может быть null. Учтено в getEndTime() - хорошо.++++
    protected Duration duration;
    // YELLOW: Может быть null. Учтено в getEndTime() - хорошо.+++++
    protected LocalDateTime startTime;


    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ZERO;
        // RED: Критично! Поля duration и startTime остаются null.+++++
        // getEndTime() будет возвращать null. Это нужно либо явно задокументировать,++++
        // либо инициализировать значениями по умолчанию.++++
    }

    public Task(long id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }
    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    // YELLOW: Сеттер для ID. Идентификатор не должен меняться после создания.
    // Это может сломать логику менеджера, который relies на неизменности ID.
    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        } else {
            // YELLOW: Нет проверки, что duration != null.+++
            // Если duration null, а startTime не null, выбросится NPE.+++
            // Нужно: if (startTime == null || duration == null) return null;++++
            return startTime.plus(duration);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        // YELLOW: В toString не выводятся важные поля duration и startTime.+++
        // Это усложнит отладку временных характеристик.+++++
        return "Task{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", ID=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", duration=" + getDuration() + '\'' +
                ", startTime=" + getStartTime() +
                '}';
    }
}