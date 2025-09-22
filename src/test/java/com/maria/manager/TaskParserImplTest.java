package com.maria.manager;

import com.maria.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskParserImplTest{

    private final TaskParserImpl parser = new TaskParserImpl();

    @Test
    void shouldParseTaskWithStatusFromString() {
        String line = "TASK,3,COOK,LUNCH,IN_PROCESS,PT10M,2025-09-13T15:48:05.442811600";
        Task task = parser.toTask(line);

        assertEquals(3, task.getId());
        assertEquals("COOK", task.getName());
        assertEquals("LUNCH", task.getDescription());
        assertEquals(Status.IN_PROCESS, task.getStatus());
        assertEquals(Duration.ofMinutes(10), task.getDuration());
        assertEquals(LocalDateTime.parse("2025-09-13T15:48:05.442811600"), task.getStartTime());
    }

}