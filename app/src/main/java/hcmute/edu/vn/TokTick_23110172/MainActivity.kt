package hcmute.edu.vn.TokTick_23110172

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.TokTick_23110172.adapter.SidebarAdapter
import hcmute.edu.vn.TokTick_23110172.adapter.SidebarItem
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

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var sidebarAdapter: SidebarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_main_content)

        // 1. Setup Drawer và Sidebar
        drawerLayout = findViewById(R.id.drawerLayout)
        val rvSidebarMenu = findViewById<RecyclerView>(R.id.rvSidebarMenu)
        sidebarAdapter = SidebarAdapter()
        rvSidebarMenu.adapter = sidebarAdapter
        // rvSidebarMenu đã có LayoutManager trong XML

        // 2. Setup Task List (Nội dung chính)
        val recyclerView = findViewById<RecyclerView>(R.id.rvTasks)
        val taskAdapter = TaskAdapter()
        recyclerView.adapter = taskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 3. Observe Categories để cập nhật Sidebar
        taskViewModel.allCategories.observe(this) { categories ->
            val sidebarItems = mutableListOf<SidebarItem>()
            sidebarItems.add(SidebarItem.Header("LISTS"))
            
            val menuItems = categories.map { category ->
                SidebarItem.MenuItem(category, 0) // Tạm thời để taskCount = 0
            }
            sidebarItems.addAll(menuItems)
            
            sidebarAdapter.submitList(sidebarItems)
        }

        // 4. Observe Tasks
        taskViewModel.allTasks.observe(this) { tasks ->
            taskAdapter.submitTaskList(tasks)
        }

        // 5. Điều khiển Drawer qua nút Menu (btnMenu nằm trong fragment_main_content)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // 6. Fab Add Task
        val fab = findViewById<View>(R.id.fabAdd)
        fab.setOnClickListener {
            val addTaskSheet = AddTaskBottomSheetFragment()
            addTaskSheet.show(supportFragmentManager, "AddTaskBottomSheet")
        }
    }
}
