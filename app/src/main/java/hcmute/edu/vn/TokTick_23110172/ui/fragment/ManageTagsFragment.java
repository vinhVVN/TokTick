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
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Tag;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class ManageTagsFragment extends Fragment {

    private RecyclerView rvTags;
    private ManageAdapter adapter;
    private TaskViewModel taskViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_tags, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvTags = view.findViewById(R.id.rvTags);
        adapter = new ManageAdapter();
        rvTags.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTags.setAdapter(adapter);

        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        taskViewModel.getAllTags().observe(getViewLifecycleOwner(), tags -> {
            List<ManageAdapter.Item> items = new ArrayList<>();
            for (Tag tag : tags) {
                items.add(new ManageAdapter.Item() {
                    @Override
                    public int getId() {
                        return tag.getTagId();
                    }

                    @Override
                    public String getName() {
                        return tag.getName();
                    }

                    @Override
                    public String getIcon() {
                        return "🏷️";
                    }
                });
            }
            adapter.setItems(items);
        });
    }
}
