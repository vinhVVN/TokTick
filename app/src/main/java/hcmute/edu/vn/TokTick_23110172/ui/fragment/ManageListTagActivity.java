package hcmute.edu.vn.TokTick_23110172.ui.fragment;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.dao.AppDatabase;
import hcmute.edu.vn.TokTick_23110172.repository.TaskRepository;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class ManageListTagActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton btnBack;
    private TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_list_tag);

        // Khởi tạo TaskViewModel với Factory giống như trong MainActivity
        AppDatabase database = AppDatabase.getDatabase(this);
        TaskRepository repository = new TaskRepository(database.taskDao());
        TaskViewModel.TaskViewModelFactory factory = new TaskViewModel.TaskViewModelFactory(repository);
        taskViewModel = new ViewModelProvider(this, factory).get(TaskViewModel.class);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        viewPager.setAdapter(new ManagePagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Danh sách");
                    break;
                case 1:
                    tab.setText("Thẻ");
                    break;
            }
        }).attach();
    }

    private static class ManagePagerAdapter extends FragmentStateAdapter {
        public ManagePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 1) {
                return new ManageTagsFragment();
            }
            return new ManageListsFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
