package hcmute.edu.vn.TokTick_23110172.viewmodel;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;

public class AddListDialogFragment extends DialogFragment {

    private TextInputEditText etListName;
    private RecyclerView rvIcons;
    private String selectedIcon = "ic_notes"; // Default icon
    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etListName = view.findViewById(R.id.etListName);
        rvIcons = view.findViewById(R.id.rvIcons);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);

        // Khởi tạo ViewModel
        AppDatabase database = AppDatabase.getDatabase(requireContext());
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(requireActivity(), factory).get(TaskViewModel.class);

        setupIconList();

        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveList());
    }

    private void setupIconList() {
        List<String> icons = Arrays.asList("ic_notes", "ic_alarm", "ic_attach_file", "ic_menu", "ic_sort", "ic_tune");
        IconAdapter adapter = new IconAdapter(icons, icon -> selectedIcon = icon);
        rvIcons.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvIcons.setAdapter(adapter);
    }

    private void saveList() {
        String name = etListName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập tên danh sách", Toast.LENGTH_SHORT).show();
            return;
        }

        ListCategory newCategory = new ListCategory(name, selectedIcon, false);
        taskViewModel.insertCategory(newCategory);
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

    // Adapter nội bộ cho danh sách icon
    private class IconAdapter extends RecyclerView.Adapter<IconAdapter.IconViewHolder> {
        private final List<String> icons;
        private final OnIconClickListener listener;
        private int selectedPosition = 0;

        public IconAdapter(List<String> icons, OnIconClickListener listener) {
            this.icons = icons;
            this.listener = listener;
        }

        @NonNull
        @Override
        public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon_picker, parent, false);
            return new IconViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
            String iconName = icons.get(position);
            int resId = getContext().getResources().getIdentifier(iconName, "drawable", getContext().getPackageName());
            holder.ivIcon.setImageResource(resId);
            
            holder.itemView.setSelected(selectedPosition == position);
            holder.itemView.setOnClickListener(v -> {
                int previous = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(previous);
                notifyItemChanged(selectedPosition);
                listener.onIconClick(iconName);
            });
        }

        @Override
        public int getItemCount() {
            return icons.size();
        }

        class IconViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            public IconViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.ivIcon);
            }
        }
    }

    interface OnIconClickListener {
        void onIconClick(String iconName);
    }
}