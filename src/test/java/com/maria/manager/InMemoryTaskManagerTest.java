package com.maria.manager;

import com.maria.model.Task;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest {

    public InMemoryTaskManagerTest() {
        taskManager = Managers.createTaskManager();
    }
}