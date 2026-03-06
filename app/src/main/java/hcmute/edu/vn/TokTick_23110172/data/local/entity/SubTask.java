package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "subtasks",
        foreignKeys = @ForeignKey(
                entity = Task.class,
                parentColumns = "id",
                childColumns = "taskId",
                onDelete = CASCADE
        )
)
public class SubTask {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int taskId;
    private String title;
    private boolean isCompleted;

    public SubTask(int taskId, String title, boolean isCompleted) {
        this.taskId = taskId;
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public SubTask() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}