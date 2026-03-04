package hcmute.edu.vn.TokTick_23110172.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    // Chuyển đổi Flow từ Repository sang LiveData để UI dễ dàng quan sát (Observe)
    val allTasks: LiveData<List<Task>> = repository.allTasks.asLiveData()

    // Hàm gọi khi người dùng bấm nút "Thêm"
    fun insert(task: Task) {
        // viewModelScope giúp chạy tác vụ nặng ngầm định mà không làm đơ UI
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }
}

// Factory để khởi tạo ViewModel có truyền tham số (Repository)
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}