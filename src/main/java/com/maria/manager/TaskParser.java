package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;

public interface TaskParser {

    String toString(Task task);
    String toString(Subtask subtask);
    String toString(Epic epic);

    Task toTask(String string);
    Subtask toSubtask(String string);
    Epic toEpic(String string);




}
