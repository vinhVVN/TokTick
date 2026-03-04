package hcmute.edu.vn.TokTick_23110172.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_categories")
data class ListCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,         // Ví dụ: "Work Tasks", "Today"
    val iconName: String,     // Lưu tên icon để hiển thị (VD: "ic_work")
    val isSmartList: Boolean  // True nếu là Today/Next 7 Days, False nếu là List tự tạo
)