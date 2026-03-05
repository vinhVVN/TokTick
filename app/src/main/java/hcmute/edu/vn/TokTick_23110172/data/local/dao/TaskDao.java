package hcmute.edu.vn.TokTick_23110172.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

@Dao
public interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksByListId(int listId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertListCategory(ListCategory category);

    @Query("SELECT * FROM list_categories")
    LiveData<List<ListCategory>> getAllCategories();
}
