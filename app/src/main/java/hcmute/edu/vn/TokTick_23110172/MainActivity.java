package hcmute.edu.vn.TokTick_23110172;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.adapter.SidebarAdapter;
import hcmute.edu.vn.TokTick_23110172.adapter.SidebarItem;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskAdapter;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.viewmodel.AddTaskBottomSheetFragment;
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private DrawerLayout drawerLayout;
    private SidebarAdapter sidebarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main_content);

        // Khởi tạo ViewModel
        AppDatabase database = AppDatabase.getDatabase(this);
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        // 1. Setup Drawer và Sidebar
        drawerLayout = findViewById(R.id.drawerLayout);
        RecyclerView rvSidebarMenu = findViewById(R.id.rvSidebarMenu);
        sidebarAdapter = new SidebarAdapter();
        rvSidebarMenu.setAdapter(sidebarAdapter);
        rvSidebarMenu.setLayoutManager(new LinearLayoutManager(this));

        // 2. Setup Task List (Nội dung chính)
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        TaskAdapter taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 3. Observe Categories để cập nhật Sidebar
        taskViewModel.getAllCategories().observe(this, categories -> {
            List<SidebarItem> sidebarItems = new ArrayList<>();
            sidebarItems.add(new SidebarItem.Header("LISTS"));

            for (ListCategory category : categories) {
                sidebarItems.add(new SidebarItem.MenuItem(category, 0)); // Tạm thời để taskCount = 0
            }

            sidebarAdapter.submitList(sidebarItems);
        });

        // 4. Observe Tasks
        taskViewModel.getAllTasks().observe(this, tasks -> {
            taskAdapter.submitTaskList(tasks);
        });

        // 5. Điều khiển Drawer qua nút Menu
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        // 6. Fab Add Task
        View fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            AddTaskBottomSheetFragment addTaskSheet = new AddTaskBottomSheetFragment();
            addTaskSheet.show(getSupportFragmentManager(), "AddTaskBottomSheet");
        });
    }
}
