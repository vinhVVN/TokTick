package hcmute.edu.vn.TokTick_23110172.data.local.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ListCategory::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.SET_NULL // Khi xóa Category, task sẽ không bị xóa mà chỉ bị để trống listId
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val listId: Int? = null,      // Khóa ngoại có thể null
    val dueDate: Long? = null,    // Lưu ngày dưới dạng Timestamp
    val dueTime: String? = null,  // Ví dụ: "07:00"
    val isCompleted: Boolean = false,
    val hasNotes: Boolean = false,
    val hasAlarm: Boolean = false,
    val hasAttachment: Boolean = false
)
