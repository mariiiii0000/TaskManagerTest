package com.maria.manager;

import com.maria.model.TaskNotFoundException;
import com.maria.model.TaskOverlapException;
import com.maria.model.Subtask;
import com.maria.model.Epic;
import com.maria.model.Task;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.createHistoryManager();
    protected final Map<Long, Task> taskHashMap = new HashMap<>();
    protected final Map<Long, Subtask> subtaskHashMap = new HashMap<>();
    protected final Map<Long, Epic> epicHashMap = new HashMap<>();
    protected long nextID = 1;


    @Override
    public Task getTaskByID(long id) {
        if (taskHashMap.containsKey(id)){
            historyManager.add(taskHashMap.get(id));
            return taskHashMap.get(id);
        // YELLOW+++
        // Лучше выкидывать проверяемое исключение TaskNotFoundException
        } else {
            throw new TaskNotFoundException("Task ID was not found.");
        }
    }

    @Override
    public Subtask getSubtaskByID(long id) {
        if (subtaskHashMap.containsKey(id)){
            historyManager.add(subtaskHashMap.get(id));
            return subtaskHashMap.get(id);

        } else {
            throw new TaskNotFoundException("Subtask ID was not found.");
        }
    }

    @Override
    public Epic getEpicByID(long id) {
        if (epicHashMap.containsKey(id)){
            historyManager.add(epicHashMap.get(id));
            return epicHashMap.get(id);

        } else {
            throw new TaskNotFoundException("Epic ID was not found.");
        }
    }

    @Override
    public void removeTasksByID(long id) {
        if (!taskHashMap.containsKey(id)){
            throw new TaskNotFoundException("Task ID was not found.");
        }
        taskHashMap.remove(id);
        historyManager.remove(id);

    }

    @Override
    public void removeSubtaskByID(long id) {
        if (!subtaskHashMap.containsKey(id)){
            throw new TaskNotFoundException("Subtask ID was not found.");
        }
        Subtask subtask = subtaskHashMap.get(id);
        Epic epic = epicHashMap.get(subtask.getEpicID());
        epic.removeSubtaskByID(id);
        subtaskHashMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicByID(long id) {
        if (!epicHashMap.containsKey(id)){
            throw new TaskNotFoundException("Epic ID was not found.");
        }
        List<Subtask> epSubtasks = getSubtasksByEpicID(id);
        // YELLOW: Неэффективно.
        // Метод создает новый список, хотя можно работать напрямую с мапой эпика.
        for (Subtask subtask : epSubtasks) {
            subtaskHashMap.remove(subtask.getID());
            historyManager.remove(subtask.getID());
        }
        epicHashMap.remove(id);
        historyManager.remove(id);
    }

    // RED: Критично!
    // Эти методы должны только возвращать данные, без побочных эффектов.
    // Нарушение инкапсуляции. Публичные методы getTasks(), getEpics(), getSubtasks()
    // добавляют ВСЕ элементы в историю просмотров. Это абсолютно неверное поведение.
    // В историю должен попадать только тот объект, который запросили по отдельности (через get...ById).
    // Представьте, что вы просто выводите список всех задач, а они все разом попадают в историю,
    // затирая реальные последние просмотры.
    @Override
    public List<Subtask> getSubtasks() {
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtaskHashMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        for (Epic epic : epicHashMap.values()){
            historyManager.add(epic);
        }
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public List<Task> getTasks() {
        for (Task task : taskHashMap.values()){
            historyManager.add(task);
        }
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epicHashMap.values()){
            historyManager.remove(epic.getID());
        }
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.remove(subtask.getID());
        }
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.remove(subtask.getID());
        }
        subtaskHashMap.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.removeSubtasks();
        }
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.remove(subtask.getID());
        }
    }

    @Override
    public void removeTasks() {
        for (Task task : taskHashMap.values()){
            historyManager.remove(task.getID());
        }
        taskHashMap.clear();
    }

    @Override
    public void updateTask(Task newTask) {
        if (!taskHashMap.containsKey(newTask.getID())){
            throw new TaskNotFoundException("ID " + newTask.getID() + " is not found.");
        }
        taskHashMap.put(newTask.getID(), newTask);
        historyManager.remove(newTask.getID());
        historyManager.add(newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (!subtaskHashMap.containsKey(newSubtask.getID())){
            throw new TaskNotFoundException("ID " + newSubtask.getID() + " is not found.");
        }
        subtaskHashMap.put(newSubtask.getID(), newSubtask);
        Epic epic = epicHashMap.get(newSubtask.getEpicID());
        epic.updateStatus();
        historyManager.remove(newSubtask.getID());
        historyManager.add(newSubtask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (!epicHashMap.containsKey(newEpic.getID())){
            throw new TaskNotFoundException("ID " + newEpic.getID() + " is not found.");
        }
        Epic oldEpic = epicHashMap.get(newEpic.getID());
        Map<Long, Subtask> subtasks = oldEpic.getSubtasks();
        newEpic.setSubtasks(subtasks);
        epicHashMap.put(newEpic.getID(), newEpic);
        historyManager.remove(newEpic.getID());
        historyManager.add(newEpic);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(long epicID) {
        if (!epicHashMap.containsKey(epicID)){
            throw new TaskNotFoundException("Epic ID was not found.");
        }
        Epic epic = epicHashMap.get(epicID);
        return new ArrayList<>(epic.getSubtasks().values());
    }

    @Override
    public void createTask(Task task) {
        checkTime(task);
        if (task.getID() == 0){
            task.setID(nextID);
        }
        taskHashMap.put(task.getID(), task);
        nextID++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        long epicID = subtask.getEpicID();
        if (!epicHashMap.containsKey(epicID)) {
            throw new TaskNotFoundException("Epic ID is not found.");
        }
        checkTime(subtask);
        
        Epic epic = epicHashMap.get(epicID);
        if (subtask.getID() == 0){
            subtask.setID(nextID);
        }
        epic.addSubtasks(subtask);
        subtaskHashMap.put(subtask.getID(), subtask);
        nextID++;
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getID() == 0){
            epic.setID(nextID);
        }
        nextID++;
        epicHashMap.put(epic.getID(), epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // RED: Нужно проверять не только точное совпадение времени начала,
    // но и пересечение интервалов (время начала + продолжительность).
    @Override
    public void checkTime(Task newTask) {
        for (Task task : taskHashMap.values()) {
            if (newTask.getStartTime().equals(task.getStartTime())) {
                throw new TaskOverlapException("Task " + newTask.getName()
                        + " has the same start time with task " + task.getName());
            }
        }
        for (Subtask subtask : subtaskHashMap.values()) {
            if (newTask.getStartTime().equals(subtask.getStartTime())) {
                throw new TaskOverlapException("Subtask " + newTask.getName()
                        + " has the same start time with task " + subtask.getName());
            }
        }

    }
}
