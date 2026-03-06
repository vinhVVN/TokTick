package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;

@Entity(
        tableName = "task_tag_cross_ref",
        primaryKeys = {"taskId", "tagId"},
        indices = {@Index("tagId")}
)
public class TaskTagCrossRef {
    private int taskId;
    private int tagId;

    public TaskTagCrossRef(int taskId, int tagId) {
        this.taskId = taskId;
        this.tagId = tagId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
