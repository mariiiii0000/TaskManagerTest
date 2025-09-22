package com.maria.manager;

import com.maria.model.Epic;
import com.maria.model.Subtask;
import com.maria.model.Task;
import com.maria.model.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest <T extends TaskManager> {
    T TaskManager;

    Task task1;
    Task task2;
    Task task3;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    Epic epic1;
    Epic epic2;
    Epic epic3;

    TaskManager taskManager = new InMemoryTaskManager();

    @BeforeEach
    void beforeEach(){
       task1 = new Task("Task1", "1", Status.NEW, Duration.ofMinutes(10)
               , LocalDateTime.parse("2025-09-13T10:00"));
       task2 = new Task("Task2", "2", Status.NEW, Duration.ofMinutes(24)
               , LocalDateTime.parse("2025-09-13T11:00"));
       task3 = new Task("Task3", "3", Status.NEW, Duration.ofMinutes(46)
               , LocalDateTime.parse("2025-09-13T12:00"));

       epic1 = new Epic("Epic1", "1");
       epic2 = new Epic("Epic2", "2");
       epic3 = new Epic("Epic3", "3");

       subtask1 = new Subtask(0, "Subtask1", "1", Status.NEW, 1
               , Duration.ofMinutes(10), LocalDateTime.parse("2025-09-23T11:00"));
       subtask2 = new Subtask(0, "Subtask2", "2", Status.NEW, 1
               , Duration.ofMinutes(30), LocalDateTime.parse("2025-09-13T21:00"));
       subtask3 = new Subtask(0, "Subtask3", "3", Status.NEW, 1, Duration.ofMinutes(50)
               , LocalDateTime.parse("2025-10-13T11:00"));
    }

    //Task

    @Test
    public void shouldCreateTask(){
        taskManager.createTask(task1);
        final Task savedTask = taskManager.getTaskByID(1);
        assertNotNull(savedTask, "Expected not null.");
        assertEquals(savedTask, taskManager.getTaskByID(1), "Expected to be same.");

        final List<Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Expected not void.");
        assertEquals(1, tasks.size(), "Expected size is 1.");
        assertEquals(savedTask, tasks.get(0), "Expected to be same.");
    }
    @Test
    public void shouldCreateTasksWithID(){
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        final List<Task> tasks = taskManager.getTasks();
        assertEquals(3, tasks.size(), "Expected size is 3.");
        assertNotNull(tasks.get(0), "Expected not null.");
        assertNotNull(tasks.get(1), "Expected not null.");
        assertNotNull(tasks.get(2), "Expected not null.");
        assertEquals(tasks.get(0).getId(), task1.getId(), "Expected ID is 1.");
        assertEquals(tasks.get(1).getId(), task2.getId(), "Expected ID is 2.");
        assertEquals(tasks.get(2).getId(), task3.getId(), "Expected ID is 3.");
    }
    @Test
    public void createdTaskShouldBeInHashMap(){
        taskManager.createTask(task1);
        Task savedtask = taskManager.getTaskByID(task1.getId());
        assertEquals(savedtask, task1);
    }
    @Test
    void createTaskNextIDShouldIncrease(){
        task1.setId(0);
        long oldID = task1.getId();
        taskManager.createTask(task1);
        assertTrue(oldID < task1.getId());

    }
    @Test
    void shouldCreateTaskWithUniqueStartTime() {
        assertDoesNotThrow(() -> taskManager.createTask(task1));
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }


    //Subtask

    @Test
    void createSubtaskShouldReturnExceptionIfNoEpic(){
        subtask1.setEpicId(1000);
        TaskNotFoundException exception= assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.createSubtask(subtask1));
        assertEquals("Epic ID is not found.", exception.getMessage());
    }
    @Test
    void createdSubtaskShouldBeInEpicAndHashmap() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);
        assertEquals(subtask1, taskManager.getSubtaskByID(subtask1.getId()));
        assertEquals(epic1.getSubtasks().get(subtask1.getId()), subtask1);
    }
    @Test
    void createdSubtaskShouldGetIDIfIDIsNull(){
        taskManager.createEpic(epic2);
        subtask2.setEpicId(epic2.getId());
        taskManager.createSubtask(subtask2);
        assertNotEquals(0, subtask2.getId());
    }
    @Test
    void createSubtaskNextIDShouldIncrease(){
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        subtask1.setId(0);
        long oldID = subtask1.getId();
        taskManager.createSubtask(subtask1);
        assertTrue(oldID < subtask1.getId());
    }
    @Test
    void shouldCreateSubtaskWithUniqueStartTime() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        assertDoesNotThrow(() -> taskManager.createSubtask(subtask1));
    }

    //Epic
    @Test
    void createEpicIfIDIsNull(){
        epic1.setId(0);
        taskManager.createEpic(epic1);
        assertNotEquals(0, epic1.getId());
    }
    @Test
    void createEpicWithID(){
        epic1.setId(30);
        taskManager.createEpic(epic1);
        assertEquals(epic1.getId(), 30);
    }
    @Test
    void createEpicNextIDShouldIncrease(){
        epic1.setId(0);
        long oldID = epic1.getId();
        taskManager.createEpic(epic1);
        assertTrue(oldID < epic1.getId());
    }

    //Remove Epics

    @Test
    void removeEpicsShouldCleanHashMapAndSubtasks() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeEpics();
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }
    @Test
    void removeEpicsShouldCleanHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeEpics();
        assertTrue(taskManager.getHistory().isEmpty());
    }

    //Remove Subtasks

    @Test
    void removeSubtasksShouldCleanHashMapAndHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }
    @Test
    void removeAllSubtasksShouldCleanHashMapAndEpicAndHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
        assertTrue(taskManager.getEpicByID(epic1.getId()).getSubtasks().isEmpty());
    }


    //Remove Tasks

    @Test
    void removeTasksShouldCleanHashMapAndHistory() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.removeTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    //RemoveAllSubtasks

    @Test
    void shouldDeleteAllSubtasksFromHistoryAndEpic() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getHistory().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
        assertTrue(epic1.getSubtasks().isEmpty());
    }


    //UpdateTasks

    @Test
    void updateTaskShouldThrowExceptionIfIDIsWrong() {
        task1.setId(888);
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.updateTask(task1));
        assertEquals(exception.getMessage(), "ID 888 is not found.");
    }
    @Test
    void updateTaskShouldBeInHashMapAndInHistory() {
        taskManager.createTask(task1);

        Task updatedTask = task2;
        updatedTask.setId(task1.getId());
        taskManager.updateTask(task2);
        Task newTask = taskManager.getTaskByID(task1.getId());
        assertEquals(updatedTask, newTask);
        assertTrue(taskManager.getHistory().contains(updatedTask));
    }

    //UpdateSubtasks

    @Test
    void updateSubtaskShouldThrowExceptionIfIDIsWrong() {
        taskManager.createEpic(epic1);
        subtask1.setId(888);
        subtask1.setEpicId(epic1.getId());
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.updateSubtask(subtask1));
        assertEquals(exception.getMessage(), "ID 888 is not found.");
    }
    @Test
    void updateSubtaskShouldBeInHashMapAndInHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask updatedSubtask = new Subtask(0, "UpdSubtask", "UPD", Status.NEW, epic1.getId()
                , Duration.ofMinutes(10), LocalDateTime.now());
        updatedSubtask.setId(subtask1.getId());

        taskManager.updateSubtask(updatedSubtask);
        Subtask newSubtask = taskManager.getSubtaskByID(subtask1.getId());
        assertEquals(updatedSubtask, newSubtask);
        assertTrue(taskManager.getHistory().contains(updatedSubtask));
    }

    //UpdateEpics
    @Test
    void updateEpicShouldThrowExceptionIfIDIsWrong() {
        epic1.setId(444);
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.updateEpic(epic1));
        assertEquals(exception.getMessage(), "ID " + epic1.getId() + " is not found.");
    }
    @Test
    void updateEpicShouldBeInHistoryAndHashMap(){
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic updEpic = new Epic("UPDEpic", "upd");
        updEpic.setId(epic1.getId());
        taskManager.updateEpic(updEpic);
        Epic updated = taskManager.getEpicByID(epic1.getId());

        assertEquals(updated, updEpic);
        assertTrue(taskManager.getHistory().contains(updated));
    }
    @Test
    void updateEpicShouldSaveSubtasks(){
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        subtask2.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic updEpic = new Epic("UPDEpic", "upd");
        updEpic.setId(epic1.getId());
        taskManager.updateEpic(updEpic);
        Epic updated = taskManager.getEpicByID(epic1.getId());

        assertEquals(epic1.getSubtasks(), updated.getSubtasks());
    }

    //Get subtask by epic ID

    @Test
    void shouldThrowExceptionIfIDIsWrong(){
        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.getSubtasksByEpicID(999));
        assertEquals(exception.getMessage(), "Epic ID was not found.");

    }
    @Test
    void shouldReturnSameEpic(){
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());

        taskManager.createSubtask(subtask1);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicID(epic1.getId());

        assertEquals(subtasks.getFirst(), subtask1);
    }

    //Get Epics
    @Test
    void getEpicsShouldBeInHashMapAndHistory() {
        taskManager.createEpic(epic1);

        taskManager.getEpics();

        assertTrue(taskManager.getEpics().contains(epic1));
        assertTrue(taskManager.getHistory().contains(epic1));
    }

    //Get Subtasks
    @Test
    void getSubtasksShouldBeInHashMapAndHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.getSubtasks();

        assertTrue(taskManager.getSubtasks().contains(subtask1));
        assertTrue(taskManager.getHistory().contains(subtask1));
    }

    //Get Epic by ID

    @Test
    void getEpicByIDShouldThrowExceptionIfEpicIDIsWrong() {
        taskManager.createEpic(epic1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.getEpicByID(999));

        assertEquals(exception.getMessage(), "Epic ID was not found.");
    }

    //Get Subtask by ID

    @Test
    void getSubtaskByIDShouldThrowExceptionIfEpicIDIsWrong() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.getSubtaskByID(999));

        assertEquals(exception.getMessage(), "Subtask ID was not found.");
    }

    //Get Epic by ID

    @Test
    void getTaskByIDShouldThrowExceptionIfEpicIDIsWrong() {
        taskManager.createTask(task1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.getTaskByID(999));

        assertEquals(exception.getMessage(), "Task ID was not found.");
    }

    //Remove Epic by ID

    @Test
    void removeEpicByIDShouldThrowExceptionWhenIDIsWrong() {
        taskManager.createEpic(epic1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.removeEpicByID(999));

        assertEquals(exception.getMessage(), "Epic ID was not found.");
    }

    @Test
    void removeEpicByIDShouldRemoveAllSubtasksFromHashMapAndHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeEpicByID(epic1.getId());
        assertFalse(taskManager.getSubtasks().contains(subtask1));
        assertFalse(taskManager.getHistory().contains(subtask1));
    }
    @Test
    void removeEpicByIDShouldRemoveEpicFromHashMapAndHistory() {
        taskManager.createEpic(epic1);

        taskManager.removeEpicByID(epic1.getId());
        assertFalse(taskManager.getEpics().contains(epic1));
        assertFalse(taskManager.getHistory().contains(epic1));
    }

    //Remove Subtask by ID

    @Test
    void removeSubtaskByIDShouldThrowExceptionWhenIDIsWrong() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.removeSubtaskByID(999));

        assertEquals(exception.getMessage(), "Subtask ID was not found.");
    }

    @Test
    void removeSubtaskByIDShouldRemoveSubtaskFromHashMapAndHistory() {
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        taskManager.removeSubtaskByID(subtask1.getId());
        assertFalse(taskManager.getSubtasks().contains(subtask1));
        assertFalse(taskManager.getHistory().contains(subtask1));
    }

    //Remove Task by ID

    @Test
    void removeTaskByIDShouldThrowExceptionWhenIDIsWrong() {
        taskManager.createTask(task1);

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class
                , ( ) -> taskManager.removeTasksByID(999));

        assertEquals(exception.getMessage(), "Task ID was not found.");
    }

    @Test
    void removeTaskByIDShouldRemoveTaskFromHashMapAndHistory() {
        taskManager.createTask(task1);

        taskManager.removeTasksByID(task1.getId());
        assertFalse(taskManager.getTasks().contains(task1));
        assertFalse(taskManager.getHistory().contains(task1));
    }

    @Test
    void shouldReturnStartTimeAndDuration() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        subtask1.setEpicId(epic1.getId());
        taskManager.createSubtask(subtask1);

        assertEquals(task1.getStartTime(), LocalDateTime.parse("2025-09-13T10:00"));
        assertEquals(epic1.getStartTime(), LocalDateTime.parse("2025-09-23T11:00"));

        assertEquals(task1.getDuration().getSeconds()/60, 10);
        assertEquals(epic1.getDuration().getSeconds()/60, 10);
    }
}
