package hcmute.edu.vn.TokTick_23110172.viewmodel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;

public class AddTagDialogFragment extends DialogFragment {

    private TextInputEditText etTagName;
    private RadioGroup rgColors;
    private TaskViewModel taskViewModel;
    private String selectedColor = "#5C6BC0"; // Default blue

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etTagName = view.findViewById(R.id.etTagName);
        rgColors = view.findViewById(R.id.rgColors);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        // Khởi tạo ViewModel
        AppDatabase database = AppDatabase.getDatabase(requireContext());
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(requireActivity(), factory).get(TaskViewModel.class);

        rgColors.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBlue) selectedColor = "#5C6BC0";
            else if (checkedId == R.id.rbRed) selectedColor = "#EF5350";
            else if (checkedId == R.id.rbYellow) selectedColor = "#FFEE58";
            else if (checkedId == R.id.rbPurple) selectedColor = "#AB47BC";
        });

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveTag());
    }

    private void saveTag() {
        String name = etTagName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên thẻ", Toast.LENGTH_SHORT).show();
            return;
        }

        Tag newTag = new Tag(name, selectedColor);
        taskViewModel.insertTag(newTag);
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
