package hcmute.edu.vn.TokTick_23110172

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.TokTick_23110172.R
import hcmute.edu.vn.TokTick_23110172.adapter.TaskAdapter
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModel
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {

    // Khởi tạo Database, Repository và ViewModel
    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TaskRepository(database.taskDao()) }
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_content) // Thay bằng tên layout file của bạn

        // 1. Cài đặt RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        val adapter = TaskAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 2. Lắng nghe dữ liệu (Observe) từ ViewModel
        taskViewModel.allTasks.observe(this) { tasks ->
            // Cập nhật giao diện mỗi khi Database thay đổi!
            adapter.submitList(tasks)
        }

        // 3. (Tùy chọn) Thử nhét một dữ liệu giả vào để xem nó hiện lên không
        val fab = findViewById<View>(R.id.fabAdd)
        fab.setOnClickListener {
            val newTask = Task(title = "Nghe nhạc J97", listId = 1, dueTime = "10:00")
            taskViewModel.insert(newTask)
        }
    }
}