package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest <FileBackedTaskManager>{


    FileBackedTaskManager taskManager = new FileBackedTaskManager
           ("src/test/resources/manager 1/dataTo.csv", "src/test/resources/manager 1/dataFrom");

    @AfterEach
    void afterEach(){

    }

    //Get path to/from
    @Test
    void shouldReturnPathTo() {
        String path = taskManager.getPathTo();
        assertEquals(path, "src/test/resources/manager 1/dataTo.csv");
    }
    @Test
    void shouldReturnPathFrom() {
        String path = taskManager.getPathFrom();
        assertEquals(path, "src/test/resources/manager 1/dataFrom");
    }

    private String extractHistoryFromFile(Path file) {
        String res = null;
        try {
            res = Files.readString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] lines = res.split("\n");


        int lineIndex = -1;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) {
                lineIndex = i;
                break;
            }
        }
        if (lineIndex == -1 || lineIndex == lines.length - 1) {
            return "no history.";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = lineIndex+1; i < lines.length; i++) {
            stringBuilder.append(lines[i]);
        }
        return stringBuilder.toString();
    }

    //History
    @Test
    void historyShouldBeSavedCorrectly() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");
            taskManager.createTask(task1);
            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);

            taskManager.getTaskByID(task1.getID());
            taskManager.getEpicByID(epic1.getID());
            taskManager.getSubtaskByID(subtask1.getID());


            String history = extractHistoryFromFile(tempFile);
            assertTrue(history.contains(String.valueOf(task1.getID())));
            assertTrue(history.contains(String.valueOf(epic1.getID())));
            assertTrue(history.contains(String.valueOf(subtask1.getID())));

            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Create subtask and epic
    @Test
    void createEpicAndSubtaskShouldBeSavedToFile() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);
            String content = Files.readString(tempFile);
            assertTrue(content.contains("Epic1"));
            assertTrue(content.contains("Subtask1"));
            Files.deleteIfExists(tempFile);


        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Get subtask by epic ID
    @Test
    void getSubtasksByEpicIDShouldBeSavedToFile() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);

            List<Subtask> subtaskList = taskManager.getSubtasksByEpicID(epic1.getID());

            String content = Files.readString(tempFile);
            assertTrue(subtaskList.contains(subtask1));
            assertTrue(content.contains("Subtask1"));
            Files.deleteIfExists(tempFile);


        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Get Epics, Subtasks and Tasks
    @Test
    void getSubtasksTasksAndEpicsShouldBeSavedToFile() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);
            taskManager.createTask(task1);

            taskManager.getTasks();
            taskManager.getSubtasks();
            taskManager.getEpics();

            String content = Files.readString(tempFile);
            assertTrue(content.contains("Subtask1"));
            assertTrue(content.contains("Task1"));
            assertTrue(content.contains("Epic1"));
            Files.deleteIfExists(tempFile);


        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Update Epics, Subtasks and Tasks
    @Test
    void updateSubtasksTasksAndEpicsShouldBeSavedToFile() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);
            taskManager.createTask(task1);

            Task updTask = new Task("UPDTask", "upd", Status.NEW);
            Subtask updSubtask = new Subtask(0, "UPDSubtask", "upd", Status.NEW, epic1.getID()
                    , Duration.ofMinutes(10), LocalDateTime.now());
            Epic updEpic = new Epic("UPDEpic", "upd");

            updTask.setID(task1.getID());
            updSubtask.setID(subtask1.getID());
            updEpic.setID(epic1.getID());

            taskManager.updateTask(updTask);
            taskManager.updateSubtask(updSubtask);
            taskManager.updateEpic(updEpic);

            String content = Files.readString(tempFile);
            assertTrue(content.contains("UPDSubtask"));
            assertTrue(content.contains("UPDTask"));
            assertTrue(content.contains("UPDEpic"));
            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Remove

    @Test
    void removeEpicTaskSubtaskByID() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);
            taskManager.createTask(task1);

            taskManager.removeSubtaskByID(subtask1.getID());
            taskManager.removeEpicByID(epic1.getID());
            taskManager.removeTasksByID(task1.getID());

            String content = Files.readString(tempFile);
            assertFalse(content.contains("Task1"));
            assertFalse(content.contains("Subtask1"));
            assertFalse(content.contains("Epic1"));

            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    @Test
    void removeEpicsTasksSubtasks() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);
            taskManager.createTask(task1);

            taskManager.removeSubtasks();
            taskManager.removeEpics();
            taskManager.removeTasks();

            String content = Files.readString(tempFile);
            assertFalse(content.contains("Task1"));
            assertFalse(content.contains("Subtask1"));
            assertFalse(content.contains("Epic1"));

            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }
    @Test
    void removeAllSubtasks() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");

            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);

            taskManager.removeAllSubtasks();
            String content = Files.readString(tempFile);
            assertFalse(content.contains("Subtask1"));
            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }
    }

    //Load from file
    @Test
    void loadFromFileShouldRestoreTasksAndHistory() {
        try {
            Path tempFile = Path.of("src/test/resources/manager 1/dataTo.csv");
            taskManager.createTask(task1);
            taskManager.createEpic(epic1);
            subtask1.setEpicID(epic1.getID());
            taskManager.createSubtask(subtask1);

            taskManager.getTaskByID(task1.getID());
            taskManager.getEpicByID(epic1.getID());
            taskManager.getSubtaskByID(subtask1.getID());

            FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile.toString(), tempFile.toString());

            assertEquals(1, loaded.getTasks().size());
            assertEquals(1, loaded.getEpics().size());
            assertEquals(1, loaded.getSubtasks().size());

            assertEquals("Task1", loaded.getTaskByID(task1.getID()).getName());
            assertEquals("Subtask1", loaded.getSubtaskByID(subtask1.getID()).getName());

            List<Task> history = loaded.getHistory();
            assertEquals(3, history.size());
            assertTrue(history.contains(task1));
            assertTrue(history.contains(subtask1));
            Files.deleteIfExists(tempFile);

        } catch (IOException e) {
            fail("Ошибка при работе с файлом: " + e.getMessage());
        }

    }







}