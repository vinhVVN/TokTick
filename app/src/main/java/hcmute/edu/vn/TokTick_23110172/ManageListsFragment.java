package hcmute.edu.vn.TokTick_23110172;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import hcmute.edu.vn.TokTick_23110172.adapter.ItemTouchHelperCallback;
import hcmute.edu.vn.TokTick_23110172.adapter.ListCategoryAdapter;
import hcmute.edu.vn.TokTick_23110172.ui.view.TaskViewModel;

public class ManageListsFragment extends Fragment {

    private RecyclerView rvLists;
    private ListCategoryAdapter adapter;
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
        
        taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        adapter = new ListCategoryAdapter(updatedCategories -> {
            taskViewModel.updateListCategories(updatedCategories);
        });

        rvLists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLists.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvLists);

        taskViewModel.getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            adapter.setCategories(categories);
        });
    }
}
