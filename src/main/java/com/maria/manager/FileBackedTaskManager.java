package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    protected static final TaskParser taskParser = new TaskParserImpl();
    private final String pathTo;
    private final String pathFrom;

    public FileBackedTaskManager(String to, String from) {
        this.pathTo = to;
        this.pathFrom = from;
    }

    public String getPathTo() {
        return pathTo;
    }

    public String getPathFrom() {
        return pathFrom;
    }

    private String historyToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : historyManager.getHistory()) {
            long id = task.getId();
            stringBuilder.append(id).append(",");
        }
        if (!stringBuilder.isEmpty()) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public static FileBackedTaskManager loadFromFile(String pathTo, String pathFrom){
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(pathTo, pathFrom);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(pathFrom))) {
            Map<Long, Task> tasks = new HashMap<>();
            bufferedReader.readLine();
            String line = bufferedReader.readLine();
            if (line == null){
                return fileBackedTaskManager;
            }
            while (!line.isBlank()){
                TypesOfTasks typeOfTask;
                try {
                    typeOfTask = TypesOfTasks.valueOf(line.split(",")[0]);
                } catch (IllegalStateException e) {
                    System.out.println("Wrong task type.");
                    continue;
                }
                
                switch (typeOfTask) {
                    case TASK -> {
                        Task task = taskParser.toTask(line);
                        fileBackedTaskManager.createTask(task);
                        tasks.put(task.getId(), task);
                    }
                    case SUBTASK -> {
                        Subtask subtask = taskParser.toSubtask(line);
                        fileBackedTaskManager.createSubtask(subtask);
                        tasks.put(subtask.getId(), subtask);
                    }
                    case EPIC -> {
                        Epic epic = taskParser.toEpic(line);
                        fileBackedTaskManager.createEpic(epic);
                        tasks.put(epic.getId(), epic);
                    }
                    default -> System.out.println("Неизвестный тип задачи.");
                }
                line = bufferedReader.readLine();
            }
            if (line != null) {
                line = bufferedReader.readLine();
            }
            if (line != null || line.isBlank()) {
                List<Long> ids = TaskParserImpl.historyFromString(line);
                for (Long id : ids){
                    fileBackedTaskManager.historyManager.add(tasks.get(id));
                }
                fileBackedTaskManager.save(fileBackedTaskManager.pathTo);
            }

        } catch (IOException e) {
            System.out.println("Ошибка во время чтения файла.");
        }
        return fileBackedTaskManager;
    }

    @Override
    public void removeEpics() {
        super.removeEpics();
        save(pathTo);
    }

    @Override
    public void removeSubtasks() {
        super.removeSubtasks();
        save(pathTo);
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save(pathTo);
    }

    @Override
    public void removeTasks() {
        super.removeTasks();
        save(pathTo);
    }

    @Override
    public void removeTasksByID(long id) {
        super.removeTasksByID(id);
        save(pathTo);
    }

    @Override
    public void removeSubtaskByID(long id) {
        super.removeSubtaskByID(id);
        save(pathTo);
    }

    @Override
    public void removeEpicByID(long id) {
        super.removeEpicByID(id);
        save(pathTo);
    }

    @Override
    public void createSubtask(Subtask subtask){
        super.createSubtask(subtask);
        save(pathTo);
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save(pathTo);
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save(pathTo);
    }

    @Override
    public Task getTaskByID(long id) {
        Task task = super.getTaskByID(id);
        save(pathTo);
        return task;
    }

    @Override
    public Subtask getSubtaskByID(long id) {
        Subtask subtask = super.getSubtaskByID(id);
        save(pathTo);
        return subtask;
    }

    @Override
    public Epic getEpicByID(long id) {
        Epic epic = super.getEpicByID(id);
        save(pathTo);
        return epic;
    }

    @Override
    public List<Subtask> getSubtasks() {
        List<Subtask> list = new ArrayList<>(super.getSubtasks());
        save(pathTo);
        return list;
    }

    @Override
    public List<Epic> getEpics() {
        List<Epic> list = new ArrayList<>(super.getEpics());
        save(pathTo);
        return list;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>(super.getTasks());
        save(pathTo);
        return list;
    }

    @Override
    public void updateTask(Task newTask) {
        super.updateTask(newTask);
        save(pathTo);
    }

    @Override
    public void updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save(pathTo);
    }

    @Override
    public void updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save(pathTo);
    }

    @Override
    public List<Subtask> getSubtasksByEpicID(long epicID) {
        List<Subtask> list = new ArrayList<>(super.getSubtasksByEpicID(epicID));
        save(pathTo);
        return list;
    }

    public void save(String fileName){
        try (FileWriter fileWriter = new FileWriter(fileName)){
            StringBuilder stringBuilder = new StringBuilder("type,id,name,description,status,duration,start\n");
            for (Task task: taskHashMap.values()) {
                stringBuilder.append(taskParser.toString(task)).append("\n");
            }
            for (Epic epic: epicHashMap.values()) {
                stringBuilder.append(taskParser.toString(epic)).append("\n");
            }
            for (Subtask subtask: subtaskHashMap.values()) {
                stringBuilder.append(taskParser.toString(subtask)).append("\n");
            }
            stringBuilder.append("\n");
            stringBuilder.append(historyToString());
            fileWriter.write(stringBuilder.toString());

        } catch (IOException e){
            System.out.println("Ошибка во время чтения файла.");
        }
    }

}
