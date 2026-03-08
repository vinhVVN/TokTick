package hcmute.edu.vn.TokTick_23110172.ui.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.adapter.TaskDetailSubtaskAdapter;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.SubTask;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.TaskFullDetails;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class TaskDetailFragment extends Fragment implements TaskDetailSubtaskAdapter.OnSubtaskChangeListener {

    private static final String ARG_TASK_ID = "task_id";

    private int taskId;
    private TaskViewModel taskViewModel;
    private Task currentTask;
    private List<SubTask> currentSubtasks = new ArrayList<>();
    private List<Tag> currentTags = new ArrayList<>();
    private List<ListCategory> allCategories = new ArrayList<>();
    private List<Tag> allTags = new ArrayList<>();
    
    private TaskDetailSubtaskAdapter subtaskAdapter;

    private EditText etTitle, etNotes;
    private CheckBox cbMainTask;
    private ProgressBar pbSubtask;
    private TextView tvSubtaskRatio, tvReminderValue, tvCategoryValue, tvTagsValue;
    private RecyclerView rvSubtasks;

    public static TaskDetailFragment newInstance(int taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taskId = getArguments().getInt(ARG_TASK_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_detail, container, false);

        initViews(view);
        setupViewModel();
        setupRecyclerView();
        observeTaskData();

        return view;
    }

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        ImageView btnDelete = view.findViewById(R.id.btnDeleteTask);
        btnDelete.setOnClickListener(v -> deleteTask());

        etTitle = view.findViewById(R.id.etTaskDetailTitle);
        etNotes = view.findViewById(R.id.etTaskNotes);
        cbMainTask = view.findViewById(R.id.cbTaskDetail);
        pbSubtask = view.findViewById(R.id.pbSubtaskProgress);
        tvSubtaskRatio = view.findViewById(R.id.tvSubtaskRatio);
        rvSubtasks = view.findViewById(R.id.rvDetailSubtasks);

        tvReminderValue = view.findViewById(R.id.tvReminderValue);
        tvCategoryValue = view.findViewById(R.id.tvCategoryValue);
        tvTagsValue = view.findViewById(R.id.tvTagsValue);

        view.findViewById(R.id.btnAddSubtask).setOnClickListener(v -> addNewSubtask());

        // Xử lý chọn ngày giờ
        view.findViewById(R.id.btnSetReminder).setOnClickListener(v -> showDateTimePicker());
        
        // Xử lý chọn danh mục
        view.findViewById(R.id.btnSetCategory).setOnClickListener(v -> showCategorySelectionDialog());
        
        // Xử lý chọn tag
        view.findViewById(R.id.btnSetTags).setOnClickListener(v -> showTagSelectionDialog());
    }

    private void setupViewModel() {
        AppDatabase database = AppDatabase.getDatabase(requireContext());
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);
    }

    private void setupRecyclerView() {
        subtaskAdapter = new TaskDetailSubtaskAdapter(this);
        rvSubtasks.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSubtasks.setAdapter(subtaskAdapter);
    }

    private void observeTaskData() {
        // Lấy thông tin đầy đủ của task (bao gồm category, subtasks, tags)
        taskViewModel.getTaskFullDetailsById(taskId).observe(getViewLifecycleOwner(), taskFullDetails -> {
            if (taskFullDetails != null) {
                currentTask = taskFullDetails.task;
                currentSubtasks = taskFullDetails.subTasks != null ? new ArrayList<>(taskFullDetails.subTasks) : new ArrayList<>();
                currentTags = taskFullDetails.tags != null ? new ArrayList<>(taskFullDetails.tags) : new ArrayList<>();
                
                displayTaskData(taskFullDetails);
            }
        });

        // Lấy danh sách tất cả category để user chọn
        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            allCategories = categories;
            updateCategoryDisplay();
        });

        // Lấy danh sách tất cả tags để user chọn
        taskViewModel.getAllTags().observe(getViewLifecycleOwner(), tags -> {
            allTags = tags;
            updateTagsDisplay();
        });
    }

    private void displayTaskData(TaskFullDetails details) {
        if (currentTask == null) return;

        etTitle.setText(currentTask.getTitle());
        etNotes.setText(currentTask.getNotes() != null ? currentTask.getNotes() : "");
        cbMainTask.setChecked(currentTask.isCompleted());
        
        updateDateTimeDisplay();
        updateCategoryDisplay(details.getCategory());
        updateTagsDisplay();

        subtaskAdapter.setSubtasks(currentSubtasks);
        updateProgress();
    }

    private void updateDateTimeDisplay() {
        if (currentTask.getDueDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateStr = sdf.format(new Date(currentTask.getDueDate()));
            String timeStr = currentTask.getDueTime() != null ? currentTask.getDueTime() : "";
            tvReminderValue.setText(dateStr + " " + timeStr);
        } else {
            tvReminderValue.setText("Thêm ngày giờ");
        }
    }

    private void updateCategoryDisplay() {
        if (currentTask == null) return;
        
        if (currentTask.getListId() != null) {
            for (ListCategory category : allCategories) {
                if (category.getId() == currentTask.getListId()) {
                    tvCategoryValue.setText(category.getName());
                    return;
                }
            }
        }
        tvCategoryValue.setText("Chọn danh mục");
    }

    private void updateCategoryDisplay(ListCategory category) {
        if (category != null) {
            tvCategoryValue.setText(category.getName());
        } else {
            tvCategoryValue.setText("Chọn danh mục");
        }
    }

    private void updateTagsDisplay() {
        if (currentTags == null || currentTags.isEmpty()) {
            tvTagsValue.setText("Thêm thẻ (Tag)");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < currentTags.size(); i++) {
                sb.append(currentTags.get(i).getName());
                if (i < currentTags.size() - 1) sb.append(", ");
            }
            tvTagsValue.setText(sb.toString());
        }
    }

    private void showCategorySelectionDialog() {
        if (allCategories.isEmpty()) {
            Toast.makeText(getContext(), "Không có danh mục nào!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] categoryNames = new String[allCategories.size()];
        int selectedIndex = -1;
        for (int i = 0; i < allCategories.size(); i++) {
            categoryNames[i] = allCategories.get(i).getName();
            if (currentTask != null && currentTask.getListId() != null && allCategories.get(i).getId() == currentTask.getListId()) {
                selectedIndex = i;
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn danh mục")
                .setSingleChoiceItems(categoryNames, selectedIndex, (dialog, which) -> {
                    ListCategory selected = allCategories.get(which);
                    currentTask.setListId(selected.getId());
                    tvCategoryValue.setText(selected.getName());
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showTagSelectionDialog() {
        if (allTags.isEmpty()) {
            Toast.makeText(getContext(), "Không có thẻ nào!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] tagNames = new String[allTags.size()];
        boolean[] checkedItems = new boolean[allTags.size()];
        
        for (int i = 0; i < allTags.size(); i++) {
            tagNames[i] = allTags.get(i).getName();
            // Kiểm tra xem tag này đã được chọn chưa
            for (Tag t : currentTags) {
                if (t.getTagId() == allTags.get(i).getTagId()) {
                    checkedItems[i] = true;
                    break;
                }
            }
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn thẻ (Tag)")
                .setMultiChoiceItems(tagNames, checkedItems, (dialog, which, isChecked) -> {
                    Tag tag = allTags.get(which);
                    if (isChecked) {
                        // Thêm nếu chưa có
                        boolean exists = false;
                        for (Tag t : currentTags) {
                            if (t.getTagId() == tag.getTagId()) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) currentTags.add(tag);
                    } else {
                        // Xóa
                        for (int i = 0; i < currentTags.size(); i++) {
                            if (currentTags.get(i).getTagId() == tag.getTagId()) {
                                currentTags.remove(i);
                                break;
                            }
                        }
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    updateTagsDisplay();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();
        if (currentTask.getDueDate() != null) {
            calendar.setTimeInMillis(currentTask.getDueDate());
        }

        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                currentTask.setDueDate(calendar.getTimeInMillis());
                currentTask.setDueTime(time);
                updateDateTimeDisplay();
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateProgress() {
        if (currentSubtasks.isEmpty()) {
            pbSubtask.setProgress(0);
            tvSubtaskRatio.setText("0/0 bước");
            return;
        }

        int completedCount = 0;
        for (SubTask sub : currentSubtasks) {
            if (sub.isCompleted()) completedCount++;
        }

        int total = currentSubtasks.size();
        int progress = (completedCount * 100) / total;
        pbSubtask.setProgress(progress);
        tvSubtaskRatio.setText(completedCount + "/" + total + " bước");
    }

    private void addNewSubtask() {
        SubTask newSub = new SubTask(taskId, "", false);
        currentSubtasks.add(newSub);
        subtaskAdapter.setSubtasks(new ArrayList<>(currentSubtasks));
        updateProgress();
    }

    private void deleteTask() {
        if (currentTask != null) {
            taskViewModel.delete(currentTask);
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveChanges();
    }

    private void saveChanges() {
        if (currentTask == null) return;

        currentTask.setTitle(etTitle.getText().toString());
        currentTask.setNotes(etNotes.getText().toString());
        currentTask.setCompleted(cbMainTask.isChecked());

        taskViewModel.updateTaskFullDetails(currentTask, currentSubtasks, currentTags);
    }

    @Override
    public void onSubtaskStatusChanged(SubTask subtask, boolean isCompleted) {
        updateProgress();
    }

    @Override
    public void onSubtaskTitleChanged(SubTask subtask, String newTitle) {
        // Data updated via adapter
    }

    @Override
    public void onSubtaskDeleted(SubTask subtask) {
        currentSubtasks.remove(subtask);
        taskViewModel.deleteSubTask(subtask);
        subtaskAdapter.setSubtasks(new ArrayList<>(currentSubtasks));
        updateProgress();
    }
}
