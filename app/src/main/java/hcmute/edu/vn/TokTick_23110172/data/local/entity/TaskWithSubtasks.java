package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class TaskWithSubtasks {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "id",
            entityColumn = "taskId"
    )
    public List<SubTask> subTasks;
}