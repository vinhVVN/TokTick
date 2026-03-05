package hcmute.edu.vn.TokTick_23110172.viewmodel;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

public class AddTaskBottomSheetFragment extends BottomSheetDialogFragment {

    private TaskViewModel taskViewModel;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault());

    private Integer selectedListId = null;
    private List<ListCategory> fullCategoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_task_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ViewModel (Chia sẻ với Activity)
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        EditText etTaskTitle = view.findViewById(R.id.etTaskTitle);
        TextView tvSelectTime = view.findViewById(R.id.tvSelectTime);
        ImageView btnSaveTask = view.findViewById(R.id.btnSaveTask);
        Spinner spinnerCategory = view.findViewById(R.id.spinnerCategory);

        tvSelectTime.setText(dateFormatter.format(calendar.getTime()));
        tvSelectTime.setOnClickListener(v -> showDateTimePicker(tvSelectTime));

        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            fullCategoryList = categories;

            List<String> displayNames = new ArrayList<>();
            for (ListCategory category : fullCategoryList) {
                displayNames.add(category.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, displayNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);

            // Mặc định chọn Inbox
            int defaultIndex = -1;
            for (int i = 0; i < fullCategoryList.size(); i++) {
                if (!fullCategoryList.get(i).isSmartList()) {
                    defaultIndex = i;
                    break;
                }
            }

            if (defaultIndex != -1) {
                spinnerCategory.setSelection(defaultIndex);
                selectedListId = fullCategoryList.get(defaultIndex).getId();
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ListCategory selectedCategory = fullCategoryList.get(position);

                if (selectedCategory.isSmartList()) {
                    updateDateBySmartList(selectedCategory.getName(), tvSelectTime);
                    selectedListId = null;
                } else {
                    selectedListId = selectedCategory.getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSaveTask.setOnClickListener(v -> {
            String title = etTaskTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Task newTask = new Task(
                        title,
                        selectedListId,
                        calendar.getTimeInMillis(),
                        timeFormatter.format(calendar.getTime()),
                        false,
                        false,
                        false,
                        false
                );
                taskViewModel.insert(newTask);
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDateBySmartList(String name, TextView tvSelectTime) {
        Calendar now = Calendar.getInstance();
        switch (name) {
            case "Today":
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR));
                break;
            case "Tomorrow":
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
                break;
            case "Next 7 Days":
                calendar.set(Calendar.YEAR, now.get(Calendar.YEAR));
                calendar.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) + 1);
                break;
        }
        tvSelectTime.setText(dateFormatter.format(calendar.getTime()));
    }

    private void showDateTimePicker(TextView tvSelectTime) {
        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                tvSelectTime.setText(dateFormatter.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    ).show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
