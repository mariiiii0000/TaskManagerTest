package com.maria.model;

import com.maria.manager.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    // YELLOW: Имя переменной 'epicID' нарушает конвенцию.
    // Лучше использовать lowerCamelCase: 'epicId'.
    // Также стоит рассмотреть возможность сделать поле final.
    private long epicID;

    // RED: Закомментированный код лучше удалять из продакшн-версии.
// Он загромождает код и может вводить в заблуждение.
//    public Subtask(String name, String description, Status status, long epicID) {
//        super(name, description, status);
//        this.epicID = epicID;
//    }

    public Subtask(long ID, String name, String description, Status status, long epicID, Duration duration
            , LocalDateTime startTime) {
        super(ID, name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public long getEpicID() {
        return epicID;
    }

    // RED: Критично! Изменение epicID после создания подзадачи
    // должно быть запрещено или как минимум сопровождаться
    // сложной логикой обновления в менеджере (старый эпик удаляет,
    // новый эпик добавляет). Лучше сделать поле final и удалить сеттер.
    public void setEpicID(long epicID) {
        this.epicID = epicID;
    }

    // YELLOW: Прямой доступ к protected-полям родителя. Лучше использовать геттеры
    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ID=" + ID +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicID == subtask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }
}
