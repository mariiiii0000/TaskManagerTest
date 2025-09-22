package com.maria.model;

import com.maria.manager.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    // YELLOW: Имя переменной 'epicID' нарушает конвенцию.+++
    // Лучше использовать lowerCamelCase: 'epicId'.+++
    // Также стоит рассмотреть возможность сделать поле final.+++
    private long epicId;

    // RED: Закомментированный код лучше удалять из продакшн-+++
// Он загромождает код и может вводить в заблуждение.+++

    public Subtask(long ID, String name, String description, Status status, long epicId, Duration duration
            , LocalDateTime startTime) {
        super(ID, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    // RED: Критично! Изменение epicID после создания подзадачи+++++
    // должно быть запрещено или как минимум сопровождаться
    // сложной логикой обновления в менеджере (старый эпик удаляет,
    // новый эпик добавляет). Лучше сделать поле final и удалить сеттер.
    public final void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    // YELLOW: Прямой доступ к protected-полям родителя. Лучше использовать геттеры+++
    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", ID=" + getId() +
                ", status='" + getStatus() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
