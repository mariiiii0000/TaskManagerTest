package com.maria.manager;

import com.maria.model.TaskNotFoundException;
import com.maria.model.TaskOverlapTimeException;
import com.maria.model.Subtask;
import com.maria.model.Epic;
import com.maria.model.Task;

import java.time.ZoneOffset;
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
        Epic epic = epicHashMap.get(subtask.getEpicId());
        epic.removeSubtaskByID(id);
        subtaskHashMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpicByID(long id) {
        if (!epicHashMap.containsKey(id)){
            throw new TaskNotFoundException("Epic ID was not found.");
        }
        Epic epic = epicHashMap.get(id);
        // YELLOW: Неэффективно.+++++
        // Метод создает новый список, хотя можно работать напрямую с мапой эпика.
        for (Subtask subtask : epic.getSubtasks().values()) {
            subtaskHashMap.remove(subtask.getId());
            historyManager.remove(subtask.getId());
        }
        epicHashMap.remove(id);
        historyManager.remove(id);
    }

    // RED: Критично!++++++++
    // Эти методы должны только возвращать данные, без побочных эффектов.
    // Нарушение инкапсуляции. Публичные методы getTasks(), getEpics(), getSubtasks()
    // добавляют ВСЕ элементы в историю просмотров. Это абсолютно неверное поведение.
    // В историю должен попадать только тот объект, который запросили по отдельности (через get...ById).
    // Представьте, что вы просто выводите список всех задач, а они все разом попадают в историю,
    // затирая реальные последние просмотры.
    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public void removeEpics() {
        for (Epic epic : epicHashMap.values()){
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.remove(subtask.getId());
        }
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    @Override
    public void removeSubtasks() {
        for (Subtask subtask : subtaskHashMap.values()){
            historyManager.remove(subtask.getId());
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
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void removeTasks() {
        for (Task task : taskHashMap.values()){
            historyManager.remove(task.getId());
        }
        taskHashMap.clear();
    }

    @Override
    public void updateTask(Task newTask) {
        if (!taskHashMap.containsKey(newTask.getId())){
            throw new TaskNotFoundException("ID " + newTask.getId() + " is not found.");
        }
        taskHashMap.put(newTask.getId(), newTask);
        historyManager.remove(newTask.getId());
        historyManager.add(newTask);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        if (!subtaskHashMap.containsKey(newSubtask.getId())){
            throw new TaskNotFoundException("ID " + newSubtask.getId() + " is not found.");
        }
        subtaskHashMap.put(newSubtask.getId(), newSubtask);
        Epic epic = epicHashMap.get(newSubtask.getEpicId());
        epic.updateStatus();
        historyManager.remove(newSubtask.getId());
        historyManager.add(newSubtask);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        if (!epicHashMap.containsKey(newEpic.getId())){
            throw new TaskNotFoundException("ID " + newEpic.getId() + " is not found.");
        }
        Epic oldEpic = epicHashMap.get(newEpic.getId());
        Map<Long, Subtask> subtasks = oldEpic.getSubtasks();
        newEpic.setSubtasks(subtasks);
        epicHashMap.put(newEpic.getId(), newEpic);
        historyManager.remove(newEpic.getId());
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
        if (task.getId() == 0){
            task.setId(nextID);
        }
        taskHashMap.put(task.getId(), task);
        nextID++;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        long epicID = subtask.getEpicId();
        if (!epicHashMap.containsKey(epicID)) {
            throw new TaskNotFoundException("Epic ID is not found.");
        }
        checkTime(subtask);
        
        Epic epic = epicHashMap.get(epicID);
        if (subtask.getId() == 0){
            subtask.setId(nextID);
        }
        epic.addSubtasks(subtask);
        subtaskHashMap.put(subtask.getId(), subtask);
        nextID++;
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic.getId() == 0){
            epic.setId(nextID);
        }
        nextID++;
        epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // RED: Нужно проверять не только точное совпадение времени начала,++++
    // но и пересечение интервалов (время начала + продолжительность).++++
    @Override
    public void checkTime(Task newTask) {
        long newStart = newTask.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long newEnd = newTask.getEndTime().toEpochSecond(ZoneOffset.UTC);

        for (Task task : taskHashMap.values()) {
            long taskStart = task.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long taskEnd = task.getEndTime().toEpochSecond(ZoneOffset.UTC);

            if (newStart < taskEnd && newEnd > taskStart) {
                throw new TaskOverlapTimeException("Task " + newTask.getName()
                        + " overlaps with task " + task.getName() + "\"");
            }
        }
        for (Subtask subtask : subtaskHashMap.values()) {
            long subStart = subtask.getStartTime().toEpochSecond(ZoneOffset.UTC);
            long subEnd = subtask.getEndTime().toEpochSecond(ZoneOffset.UTC);

            if (newStart < subEnd && newEnd > subStart) {
                throw new TaskOverlapTimeException("Subtask " + newTask.getName()
                        + "\" overlaps with subtask " + subtask.getName() + "\"");
            }
        }

    }
}
