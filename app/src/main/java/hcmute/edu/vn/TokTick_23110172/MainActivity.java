package hcmute.edu.vn.TokTick_23110172;

import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.adapter.SidebarAdapter;
import hcmute.edu.vn.TokTick_23110172.adapter.SidebarItem;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskAdapter;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskItem;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskSwipeCallback;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.viewmodel.AddListDialogFragment;
import hcmute.edu.vn.TokTick_23110172.viewmodel.AddTagDialogFragment;
import hcmute.edu.vn.TokTick_23110172.viewmodel.AddTaskBottomSheetFragment;
import hcmute.edu.vn.TokTick_23110172.viewmodel.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private DrawerLayout drawerLayout;
    private SidebarAdapter sidebarAdapter;
    private TaskAdapter taskAdapter;

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

        // Nút Thêm trong sidebar
        TextView tvAdd = findViewById(R.id.tvAdd);
        if (tvAdd != null) {
            tvAdd.setOnClickListener(v -> {
                showAddMenu(v);
            });
        }

        // Nút Tùy chỉnh trong sidebar
        ImageView ivSettings = findViewById(R.id.ivSettings);
        if (ivSettings != null) {
            ivSettings.setOnClickListener(v -> {
                Toast.makeText(this, "Tính năng Tùy chỉnh đang phát triển", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }

        // 2. Setup Task List (Nội dung chính)
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Thiết lập sự kiện click vào task để mở màn hình chi tiết
        taskAdapter.setOnTaskClickListener(task -> {
            openTaskDetail(task.getId());
        });

        // Tích hợp Swipe cho Task List
        setupSwipe(recyclerView);

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

    private void setupSwipe(RecyclerView recyclerView) {
        TaskSwipeCallback swipeCallback = new TaskSwipeCallback(this, new TaskSwipeCallback.SwipeListener() {
            @Override
            public void onSwipeToTick(int position) {
                if (position < 0 || position >= taskAdapter.getItemCount()) return;
                TaskItem item = taskAdapter.getCurrentList().get(position);
                if (item instanceof TaskItem.TaskData) {
                    Task task = ((TaskItem.TaskData) item).getTask();
                    task.setCompleted(true);
                    
                    // Hiệu ứng rung nhẹ
                    recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    
                    taskViewModel.update(task);
                    Toast.makeText(MainActivity.this, "Đã hoàn thành công việc", Toast.LENGTH_SHORT).show();
                }
                
                // LUÔN LUÔN thông báo notifyItemChanged để reset trạng thái vuốt (mất nền màu)
                taskAdapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeToDelete(int position) {
                if (position < 0 || position >= taskAdapter.getItemCount()) return;
                TaskItem item = taskAdapter.getCurrentList().get(position);
                if (item instanceof TaskItem.TaskData) {
                    Task task = ((TaskItem.TaskData) item).getTask();

                    // Sử dụng logic tạm ẩn để tránh xung đột với LiveData
                    taskAdapter.setTaskExcluded(task.getId(), true);

                    Snackbar.make(recyclerView, "Đã xóa công việc", Snackbar.LENGTH_LONG)
                            .setAction("Hoàn tác", v -> {
                                taskAdapter.setTaskExcluded(task.getId(), false);
                            })
                            .addCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar transientBottomBar, int event) {
                                    if (event != DISMISS_EVENT_ACTION) {
                                        taskViewModel.delete(task);
                                    }
                                }
                            }).show();
                } else {
                    taskAdapter.notifyItemChanged(position);
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void openTaskDetail(int taskId) {
        TaskDetailFragment detailFragment = TaskDetailFragment.newInstance(taskId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        
        // Sử dụng một ID container phù hợp. Ở đây DrawerLayout là root, 
        // nhưng chúng ta muốn đè lên nội dung chính.
        transaction.replace(android.R.id.content, detailFragment); 
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void showAddMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add(0, 1, 0, "Thêm danh sách");
        popup.getMenu().add(0, 2, 1, "Thêm thẻ (Tag)");

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                AddListDialogFragment addListDialog = new AddListDialogFragment();
                addListDialog.show(getSupportFragmentManager(), "AddListDialog");
            } else if (item.getItemId() == 2) {
                AddTagDialogFragment addTagDialog = new AddTagDialogFragment();
                addTagDialog.show(getSupportFragmentManager(), "AddTagDialog");
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        popup.show();
    }
}
