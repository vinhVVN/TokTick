package hcmute.edu.vn.TokTick_23110172.ui.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.adapter.SubtaskInputAdapter;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

public class AddTaskBottomSheetFragment extends BottomSheetDialogFragment {

    private TaskViewModel taskViewModel;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());

    private Integer selectedListId = null;
    private List<ListCategory> fullCategoryList = new ArrayList<>();
    
    private List<Tag> allTags = new ArrayList<>();
    private final List<Tag> selectedTags = new ArrayList<>();

    private SubtaskInputAdapter subtaskAdapter;
    private final List<String> subtaskList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        EditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        TextView tvSelectTime = view.findViewById(R.id.tvSelectTime);
        ImageView btnSaveTask = view.findViewById(R.id.btnSaveTask);
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);
        Chip chipSelectTags = view.findViewById(R.id.chipSelectTags);
        
        RecyclerView rvSubtasksInput = view.findViewById(R.id.rvSubtasksInput);
        ImageView btnAddSubtaskItem = view.findViewById(R.id.btnAddSubtaskItem);

        // Setup Subtasks RecyclerView
        subtaskAdapter = new SubtaskInputAdapter(subtaskList);
        rvSubtasksInput.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSubtasksInput.setAdapter(subtaskAdapter);

        btnAddSubtaskItem.setOnClickListener(v -> {
            rvSubtasksInput.setVisibility(View.VISIBLE);
            subtaskAdapter.addSubtask();
            etTaskTitle.postDelayed(() -> {
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }, 100);
        });

        tvSelectTime.setText(dateFormatter.format(calendar.getTime()));
        tvSelectTime.setOnClickListener(v -> showDateTimePicker(tvSelectTime));

        // Observe Categories
        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            fullCategoryList = categories;
            List<String> displayNames = new ArrayList<>();
            for (ListCategory category : fullCategoryList) {
                displayNames.add(category.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        });

        // Observe Tags
        taskViewModel.getAllTags().observe(getViewLifecycleOwner(), tags -> {
            allTags = tags;
        });

        chipSelectTags.setOnClickListener(v -> showTagSelectionDialog());

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Task newTask = new Task(
                        title,
                        selectedListId,
                        calendar.getTimeInMillis(),
                        timeFormatter.format(calendar.getTime()),
                        null, // notes
                        false, 
                        false, 
                        false, 
                        false
                );

                // Get current subtasks from adapter
                List<String> currentSubtasks = subtaskAdapter.getSubtasks();

                // Save Task with Tags and Subtasks
                taskViewModel.insertTaskWithDetails(newTask, selectedTags, currentSubtasks);
                
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề!", Toast.LENGTH_SHORT).show();
            }
        });
        
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < fullCategoryList.size()) {
                    ListCategory selected = fullCategoryList.get(position);
                    selectedListId = selected.isSmartList() ? null : selected.getId();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showTagSelectionDialog() {
        if (allTags.isEmpty()) {
            Toast.makeText(getContext(), "Chưa có thẻ nào, hãy tạo thẻ trước!", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] tagNames = new String[allTags.size()];
        boolean[] checkedItems = new boolean[allTags.size()];
        
        for (int i = 0; i < allTags.size(); i++) {
            tagNames[i] = allTags.get(i).getName();
            checkedItems[i] = selectedTags.contains(allTags.get(i));
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn thẻ (Tag)")
                .setMultiChoiceItems(tagNames, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedTags.add(allTags.get(which));
                    } else {
                        selectedTags.remove(allTags.get(which));
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    updateTagChip();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateTagChip() {
        if (getView() != null) {
            Chip chipSelectTags = getView().findViewById(R.id.chipSelectTags);
            if (selectedTags.isEmpty()) {
                chipSelectTags.setText("Tag");
            } else {
                chipSelectTags.setText(selectedTags.size() + " thẻ đã chọn");
            }
        }
    }

    private void showDateTimePicker(TextView tvSelectTime) {
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                tvSelectTime.setText(dateFormatter.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}