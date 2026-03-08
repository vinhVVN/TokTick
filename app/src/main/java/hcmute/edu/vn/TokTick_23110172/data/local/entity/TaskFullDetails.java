package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TaskFullDetails {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "listId",
            entityColumn = "id"
    )
    public List<ListCategory> categories;

    @Relation(
            parentColumn = "id",
            entityColumn = "taskId"
    )
    public List<SubTask> subTasks;

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

    @Ignore
    public ListCategory getCategory() {
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        return categories.get(0);
    }
}
