package hcmute.edu.vn.TokTick_23110172;

import android.content.Intent;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
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
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import hcmute.edu.vn.TokTick_23110172.adapter.SidebarAdapter;
import hcmute.edu.vn.TokTick_23110172.adapter.SidebarItem;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskAdapter;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskItem;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskSwipeCallback;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.ui.fragment.ManageListTagActivity;
import hcmute.edu.vn.TokTick_23110172.ui.fragment.TaskDetailFragment;
import hcmute.edu.vn.TokTick_23110172.ui.view.AddListDialogFragment;
import hcmute.edu.vn.TokTick_23110172.ui.view.AddTagDialogFragment;
import hcmute.edu.vn.TokTick_23110172.ui.view.AddTaskBottomSheetFragment;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private DrawerLayout drawerLayout;
    private SidebarAdapter sidebarAdapter;
    private TaskAdapter taskAdapter;

    private List<Task> fullTaskList = new ArrayList<>();
    private List<ListCategory> allCategories = new ArrayList<>();
    private int currentFilterId = SidebarItem.ID_TODAY;
    private boolean isSmartFilter = true;

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
        sidebarAdapter = new SidebarAdapter(new SidebarAdapter.OnSidebarItemClickListener() {
            @Override
            public void onSmartFilterClick(int smartFilterId) {
                currentFilterId = smartFilterId;
                isSmartFilter = true;
                applyFilter();
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            @Override
            public void onUserListClick(int listCategoryId) {
                currentFilterId = listCategoryId;
                isSmartFilter = false;
                applyFilter();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
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
                Intent intent = new Intent(this, ManageListTagActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            });
        }

        // 2. Setup Task List (Nội dung chính)
        RecyclerView recyclerView = findViewById(R.id.rvTasks);
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter.setOnTaskClickListener(task -> {
            openTaskDetail(task.getId());
        });

        setupSwipe(recyclerView);

        // 3. Observe Categories & Tasks
        taskViewModel.getAllCategories().observe(this, categories -> {
            this.allCategories = categories;
            updateSidebar();
        });

        taskViewModel.getAllTasks().observe(this, tasks -> {
            this.fullTaskList = tasks;
            updateSidebar();
            applyFilter();
        });

        // 5. Các nút điều khiển khác
        ImageView btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        View fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            AddTaskBottomSheetFragment addTaskSheet = new AddTaskBottomSheetFragment();
            addTaskSheet.show(getSupportFragmentManager(), "AddTaskBottomSheet");
        });
    }

    private void updateSidebar() {
        List<SidebarItem> sidebarItems = taskViewModel.generateSidebarItems(fullTaskList, allCategories);
        sidebarAdapter.submitList(sidebarItems);
        sidebarAdapter.setSelectedItemId(currentFilterId);
    }

    private void applyFilter() {
        List<Task> filteredList;

        if (isSmartFilter) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
            long todayStart = cal.getTimeInMillis();

            cal.add(Calendar.DAY_OF_YEAR, 1);
            long tomorrowStart = cal.getTimeInMillis();

            cal.add(Calendar.DAY_OF_YEAR, 1);
            long dayAfterTomorrowStart = cal.getTimeInMillis();

            cal.setTimeInMillis(todayStart);
            cal.add(Calendar.DAY_OF_YEAR, 7);
            long next7DaysEnd = cal.getTimeInMillis();

            cal.setTimeInMillis(todayStart);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int daysUntilNextMonday = (9 - dayOfWeek) % 7;
            if (daysUntilNextMonday == 0) daysUntilNextMonday = 7;
            cal.add(Calendar.DAY_OF_YEAR, daysUntilNextMonday);
            long nextMondayStart = cal.getTimeInMillis();

            filteredList = fullTaskList.stream().filter(task -> {
                Long dueDate = task.getDueDate();
                switch (currentFilterId) {
                    case SidebarItem.ID_TODAY:
                        return dueDate != null && dueDate >= todayStart && dueDate < tomorrowStart;
                    case SidebarItem.ID_TOMORROW:
                        return dueDate != null && dueDate >= tomorrowStart && dueDate < dayAfterTomorrowStart;
                    case SidebarItem.ID_NEXT_7_DAYS:
                        return dueDate != null && dueDate >= todayStart && dueDate < next7DaysEnd;
                    case SidebarItem.ID_THIS_WEEK:
                        return dueDate != null && dueDate >= todayStart && dueDate < nextMondayStart;
                    case SidebarItem.ID_UNSCHEDULED:
                        return dueDate == null;
                    default:
                        return true;
                }
            }).collect(Collectors.toList());
        } else {
            filteredList = fullTaskList.stream()
                    .filter(task -> task.getListId() != null && task.getListId() == currentFilterId)
                    .collect(Collectors.toList());
        }

        taskAdapter.submitTaskList(filteredList);
        
        // Cập nhật tiêu đề màn hình (nếu có TextView headerTitle)
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (tvHeaderTitle != null) {
            String title = "Tasks";
            if (isSmartFilter) {
                if (currentFilterId == SidebarItem.ID_TODAY) title = "Today";
                else if (currentFilterId == SidebarItem.ID_TOMORROW) title = "Tomorrow";
                else if (currentFilterId == SidebarItem.ID_NEXT_7_DAYS) title = "Next 7 Days";
                else if (currentFilterId == SidebarItem.ID_THIS_WEEK) title = "This Week";
                else if (currentFilterId == SidebarItem.ID_UNSCHEDULED) title = "Unscheduled";
            } else {
                for (ListCategory cat : allCategories) {
                    if (cat.getId() == currentFilterId) {
                        title = cat.getName();
                        break;
                    }
                }
            }
            tvHeaderTitle.setText(title);
        }
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
                    recyclerView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    taskViewModel.update(task);
                    Toast.makeText(MainActivity.this, "Đã hoàn thành công việc", Toast.LENGTH_SHORT).show();
                }
                taskAdapter.notifyItemChanged(position);
            }

            @Override
            public void onSwipeToDelete(int position) {
                if (position < 0 || position >= taskAdapter.getItemCount()) return;
                TaskItem item = taskAdapter.getCurrentList().get(position);
                if (item instanceof TaskItem.TaskData) {
                    Task task = ((TaskItem.TaskData) item).getTask();
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
