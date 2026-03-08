package hcmute.edu.vn.TokTick_23110172;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.ui.view.EmojiPickerBottomSheet;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class EditListActivity extends AppCompatActivity {

    private TaskViewModel taskViewModel;
    private ListCategory currentCategory;
    private EditText etListName;
    private TextView tvEditEmoji;
    private int listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        // Khởi tạo ViewModel
        AppDatabase database = AppDatabase.getDatabase(this);
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        etListName = findViewById(R.id.etListName);
        tvEditEmoji = findViewById(R.id.tvEditEmoji);
        ImageButton btnClose = findViewById(R.id.btnClose);
        ImageButton btnMore = findViewById(R.id.btnMore);

        listId = getIntent().getIntExtra("LIST_ID", -1);

        if (listId != -1) {
            taskViewModel.getListCategoryById(listId).observe(this, category -> {
                if (category != null) {
                    currentCategory = category;
                    etListName.setText(category.getName());
                    tvEditEmoji.setText(category.getIconName() != null ? category.getIconName() : "📁");
                }
            });
        }

        btnClose.setOnClickListener(v -> {
            saveAndExit();
        });

        btnMore.setOnClickListener(this::showMoreOptions);

        tvEditEmoji.setOnClickListener(v -> {
            EmojiPickerBottomSheet emojiPicker = new EmojiPickerBottomSheet();
            emojiPicker.setOnEmojiSelectedListener(emoji -> {
                tvEditEmoji.setText(emoji);
            });
            emojiPicker.show(getSupportFragmentManager(), "EmojiPicker");
        });
    }

    private void saveAndExit() {
        if (currentCategory != null) {
            String newName = etListName.getText().toString().trim();
            if (!newName.isEmpty()) {
                currentCategory.setName(newName);
                currentCategory.setIconName(tvEditEmoji.getText().toString());
                taskViewModel.updateListCategory(currentCategory);
                // Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        saveAndExit();
        super.onBackPressed();
    }

    private void showMoreOptions(android.view.View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenu().add("Xóa danh sách");
        popup.setOnMenuItemClickListener(item -> {
            if (item.getTitle().equals("Xóa danh sách")) {
                confirmDelete();
            }
            return true;
        });
        popup.show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh sách")
                .setMessage("Bạn có chắc chắn muốn xóa danh sách này không? Các công việc bên trong sẽ bị ảnh hưởng.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (currentCategory != null) {
                        taskViewModel.deleteListCategory(currentCategory);
                        Toast.makeText(this, "Đã xóa danh sách", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
