package com.maria.model;

import com.maria.manager.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    Epic epic;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void beforeEach(){
        epic = new Epic("NAME", "DSCRPT");
        subtask1 = new Subtask(0, "Subtask 1", "1", Status.NEW, 1, Duration.ofMinutes(60), LocalDateTime.now());
        subtask2 = new Subtask(0, "Subtask 2", "2", Status.NEW, 1,  Duration.ofMinutes(60), LocalDateTime.now());
    }

    @Test
    public void epicWithoutSubtasksIsNew(){
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void allSubtasksAreNewEpicShouldBeNew(){
        epic.addSubtasks(subtask1);
        epic.addSubtasks(subtask2);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void allSubtasksAreDoneEpicShouldBeDone(){
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        epic.addSubtasks(subtask1);
        epic.addSubtasks(subtask2);
        assertEquals(Status.DONE, epic.getStatus());
    }
    @Test
    public void subtasksAreNewAndDoneEpicShouldBeInProcess(){
        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);
        epic.addSubtasks(subtask1);
        epic.addSubtasks(subtask2);
        assertEquals(Status.IN_PROCESS, epic.getStatus());
    }
    @Test
    public void allSubtasksAreInProcessEpicShouldBeDone(){
        subtask1.setStatus(Status.IN_PROCESS);
        subtask2.setStatus(Status.IN_PROCESS);
        epic.addSubtasks(subtask1);
        epic.addSubtasks(subtask2);
        assertEquals(Status.IN_PROCESS, epic.getStatus());
    }


}