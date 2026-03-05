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
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModel
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModelFactory

class MainActivity : AppCompatActivity() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    private val repository by lazy { TaskRepository(database.taskDao()) }
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_content)

        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        val adapter = TaskAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        taskViewModel.allTasks.observe(this) { tasks ->
            // Sử dụng hàm mới để tự động nhóm theo ngày và hiển thị Header
            adapter.submitTaskList(tasks)
        }

        val fab = findViewById<View>(R.id.fabAdd)
        fab.setOnClickListener {
            val addTaskSheet = AddTaskBottomSheetFragment()
            addTaskSheet.show(supportFragmentManager, "AddTaskBottomSheet")
        }
    }
}
