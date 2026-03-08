package hcmute.edu.vn.TokTick_23110172.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.adapter.ManageAdapter;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class ManageListsFragment extends Fragment {

    private RecyclerView rvLists;
    private ManageAdapter adapter;
    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_lists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvLists = view.findViewById(R.id.rvLists);
        adapter = new ManageAdapter();
        rvLists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLists.setAdapter(adapter);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            List<ManageAdapter.Item> items = new ArrayList<>();
            for (ListCategory category : categories) {
                items.add(new ManageAdapter.Item() {
                    @Override
                    public String getName() {
                        return category.getName();
                    }

                    @Override
                    public String getIcon() {
                        // Trả về emoji hoặc icon tương ứng
                        return "📁"; 
                    }
                });
            }
            adapter.setItems(items);
        });
    }
}
