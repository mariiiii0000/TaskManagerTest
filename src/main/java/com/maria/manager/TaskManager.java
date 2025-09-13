package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;

import java.util.List;

public interface TaskManager {

    Task getTaskByID(long id);

    Subtask getSubtaskByID(long id);

    Epic getEpicByID(long id);


    void removeAllSubtasks();


    void removeTasksByID(long id);

    void removeSubtaskByID(long id);

    void removeEpicByID(long id);


    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Task> getTasks();


    void removeEpics();

    void removeSubtasks();

    void removeTasks();


    void updateTask(Task newTask);

    void updateSubtask(Subtask newSubtask);

    void updateEpic(Epic newEpic);


    List<Subtask> getSubtasksByEpicID(long epicID);


    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    List<Task> getHistory();

    void checkTime(Task newTask);
}
