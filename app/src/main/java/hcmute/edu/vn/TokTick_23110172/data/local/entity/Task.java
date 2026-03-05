package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tasks",
        foreignKeys = {
                @ForeignKey(
                        entity = ListCategory.class,
                        parentColumns = "id",
                        childColumns = "listId",
                        onDelete = ForeignKey.SET_NULL
                )
        }
)
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private Integer listId;      // Foreign key, nullable
    private Long dueDate;        // Timestamp, nullable
    private String dueTime;      // Example: "07:00", nullable
    private boolean isCompleted;
    private boolean hasNotes;
    private boolean hasAlarm;
    private boolean hasAttachment;

    public Task(String title, Integer listId, Long dueDate, String dueTime, boolean isCompleted, boolean hasNotes, boolean hasAlarm, boolean hasAttachment) {
        this.title = title;
        this.listId = listId;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.isCompleted = isCompleted;
        this.hasNotes = hasNotes;
        this.hasAlarm = hasAlarm;
        this.hasAttachment = hasAttachment;
    }

    public Task() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isHasNotes() {
        return hasNotes;
    }

    public void setHasNotes(boolean hasNotes) {
        this.hasNotes = hasNotes;
    }

    public boolean isHasAlarm() {
        return hasAlarm;
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
}
