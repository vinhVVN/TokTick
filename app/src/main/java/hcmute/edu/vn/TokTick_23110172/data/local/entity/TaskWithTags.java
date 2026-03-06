package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TaskWithTags {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "id",
            entityColumn = "tagId",
            associateBy = @Junction(
                    value = TaskTagCrossRef.class,
                    parentColumn = "taskId",
                    entityColumn = "tagId"
            )
    )
    public List<Tag> tags;
}
