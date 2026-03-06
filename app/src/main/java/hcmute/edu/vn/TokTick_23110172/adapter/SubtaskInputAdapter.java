package hcmute.edu.vn.TokTick_23110172.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.edu.vn.TokTick_23110172.R;

public class SubtaskInputAdapter extends RecyclerView.Adapter<SubtaskInputAdapter.ViewHolder> {

    private final List<String> subtasks;
    private int focusPosition = -1;

    public SubtaskInputAdapter(List<String> subtasks) {
        this.subtasks = subtasks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subtask_create, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.etSubtaskTitle.setText(subtasks.get(position));

        if (focusPosition == position) {
            holder.etSubtaskTitle.requestFocus();
            focusPosition = -1;
        }

        holder.etSubtaskTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                addSubtask();
                return true;
            }
            return false;
        });

        holder.etSubtaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                subtasks.set(holder.getAdapterPosition(), s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.btnDeleteSubtask.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                subtasks.remove(pos);
                notifyItemRemoved(pos);
            }
        });
    }

    public void addSubtask() {
        subtasks.add("");
        focusPosition = subtasks.size() - 1;
        notifyItemInserted(focusPosition);
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    public List<String> getSubtasks() {
        return subtasks;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText etSubtaskTitle;
        ImageView btnDeleteSubtask;

        ViewHolder(View itemView) {
            super(itemView);
            etSubtaskTitle = itemView.findViewById(R.id.etSubtaskTitle);
            btnDeleteSubtask = itemView.findViewById(R.id.btnDeleteSubtask);
        }
    }
}