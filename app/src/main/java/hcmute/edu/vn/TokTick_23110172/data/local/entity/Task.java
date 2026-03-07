package hcmute.edu.vn.TokTick_23110172.data.local.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.util.Objects;

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
    @Nullable
    private Integer listId;      // Foreign key, nullable
    @Nullable
    private Long dueDate;        // Timestamp, nullable
    @Nullable
    private String dueTime;      // Example: "07:00", nullable
    @Nullable
    private String notes;        // New field for notes
    private boolean isCompleted;
    private boolean hasNotes;
    private boolean hasAlarm;
    private boolean hasAttachment;

    public Task(String title, @Nullable Integer listId, @Nullable Long dueDate, @Nullable String dueTime, @Nullable String notes, boolean isCompleted, boolean hasNotes, boolean hasAlarm, boolean hasAttachment) {
        this.title = title;
        this.listId = listId;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.notes = notes;
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

    @Nullable
    public Integer getListId() {
        return listId;
    }

    public void setListId(@Nullable Integer listId) {
        this.listId = listId;
    }

    @Nullable
    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(@Nullable Long dueDate) {
        this.dueDate = dueDate;
    }

    @Nullable
    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(@Nullable String dueTime) {
        this.dueTime = dueTime;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                isCompleted == task.isCompleted &&
                hasNotes == task.hasNotes &&
                hasAlarm == task.hasAlarm &&
                hasAttachment == task.hasAttachment &&
                Objects.equals(title, task.title) &&
                Objects.equals(listId, task.listId) &&
                Objects.equals(dueDate, task.dueDate) &&
                Objects.equals(dueTime, task.dueTime) &&
                Objects.equals(notes, task.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, listId, dueDate, dueTime, notes, isCompleted, hasNotes, hasAlarm, hasAttachment);
    }
}
