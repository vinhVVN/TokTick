package hcmute.edu.vn.TokTick_23110172.data.local.dao

import androidx.room.*
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    // Lấy toàn bộ Task, trả về Flow để UI tự động cập nhật khi dữ liệu thay đổi
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    // Lấy Task theo một danh mục cụ thể (Ví dụ: Lấy các task của "Work Tasks")
    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY dueDate ASC")
    fun getTasksByListId(listId: Int): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListCategory(category: ListCategory): Long

    @Query("SELECT * FROM list_categories")
    fun getAllCategories(): Flow<List<ListCategory>>
}