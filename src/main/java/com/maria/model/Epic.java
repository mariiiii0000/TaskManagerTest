package com.maria.model;

import com.maria.manager.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private Map<Long, Subtask> subtasks = new HashMap<>();
    // RED: неиспользуемое поле+++++


    public Epic(String name, String description) {
        super(name, description, Status.NEW);
    }

    public Epic(String name, String description, long ID) {
        super(name, description, Status.NEW);
        // YELLOW: Прямой доступ к protected-полю ID родителя.+++
        // Лучше использовать сеттер, если он есть, или пересмотреть конструктор.++++
        this.id = ID;
    }

    public void removeSubtaskByID(long id) {
        subtasks.remove(id);
        updateStatus();

    }


    public void addSubtasks(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        updateStatus();
    }


    public void updateStatus() {
        if (subtasks.isEmpty()) {
            status = Status.NEW;
            return;
        }
        boolean hasNew = false;
        boolean hasDone = false;
        boolean hasInProcess = false;

        for (Subtask subtask : subtasks.values()) {
            switch (subtask.getStatus()) {
                case NEW -> hasNew = true;
                case IN_PROCESS -> hasInProcess = true;
                case DONE -> hasDone = true;
            }
        }

        if (hasInProcess) {
            status = Status.IN_PROCESS;
        } else if (hasNew && hasDone) {
            status = Status.IN_PROCESS;
        } else if (hasNew) {
            status = Status.NEW;
        } else if (hasDone) {
            status = Status.DONE;
        }
    }

    // RED: Закомментированный код лучше удалять из продакшн-версии.+++
    // Он загромождает код и может вводить в заблуждение.+++

    @Override
    public LocalDateTime getStartTime() {
        return subtasks.values().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public LocalDateTime getEndTime() {
        return subtasks.values().stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public Duration getDuration() {
        if (subtasks.isEmpty()) {
            return Duration.ZERO;
        }

        LocalDateTime earliestStart = subtasks.values().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime latestEnd = subtasks.values().stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        if (earliestStart == null || latestEnd == null) {
            return Duration.ZERO;
        }

        return Duration.between(earliestStart, latestEnd);
        // RED: Критичная ошибка в логике!+++++++
        // Суммируется продолжительность ВСЕХ подзадач подряд,
        // без учета их пересечений во времени.
        // Это не "duration эпика", а "суммарное время работы всех подзадач".
        // Длительность эпика - это разница между самым ранним началом
        // и самым поздним окончанием среди всех подзадач.
    }

    public void removeSubtasks() {
        subtasks.clear();
        status = Status.NEW;
    }

    // YELLOW: Нарушение инкапсуляции.+++
    // Возвращается mutable-коллекция, внешний код может её изменить.+++
    // Лучше вернуть Collections.unmodifiableMap(subtasks)+++
    // или новый HashMap<>(subtasks).+++
    public Map<Long, Subtask> getSubtasks() {
        return Collections.unmodifiableMap(subtasks);
    }

    // YELLOW: Опасно. Лучше принимать коллекцию и копировать:+++
    // this.subtasks = new HashMap<>(subtasks);+++
    public void setSubtasks(Map<Long, Subtask> subtasks) {
        this.subtasks = new HashMap<>(subtasks);
        updateStatus();
    }

    @Override
    public String toString() {
        // YELLOW: В toString не выводятся временные характеристики (startTime, duration),+++
        // которые теперь вычисляются.++
        return "Epic{" +
                "subtasks=" + getSubtasks() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", ID=" + getId() +
                ", status='" + getStatus() + '\'' +
                ", duration=" + getDuration() + '\'' +
                ", startTime=" + getStartTime() +
                '}';
    }
}



