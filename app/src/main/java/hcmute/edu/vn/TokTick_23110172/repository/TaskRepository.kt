package hcmute.edu.vn.TokTick_23110172.repository

import hcmute.edu.vn.TokTick_23110172.data.local.dao.TaskDao
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    // Lấy toàn bộ danh sách task dưới dạng Flow (luồng dữ liệu động)
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()
    
    val allCategories: Flow<List<ListCategory>> = taskDao.getAllCategories()

    // Hàm thêm mới công việc (chạy trên luồng nền - suspend)
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
}