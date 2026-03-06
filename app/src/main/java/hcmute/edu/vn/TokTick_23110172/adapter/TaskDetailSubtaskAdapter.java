package hcmute.edu.vn.TokTick_23110172.adapter;

import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.SubTask;

public class TaskDetailSubtaskAdapter extends RecyclerView.Adapter<TaskDetailSubtaskAdapter.SubtaskViewHolder> {

    private List<SubTask> subtasks = new ArrayList<>();
    private OnSubtaskChangeListener listener;

    public interface OnSubtaskChangeListener {
        void onSubtaskStatusChanged(SubTask subtask, boolean isCompleted);
        void onSubtaskTitleChanged(SubTask subtask, String newTitle);
        void onSubtaskDeleted(SubTask subtask);
    }

    public TaskDetailSubtaskAdapter(OnSubtaskChangeListener listener) {
        this.listener = listener;
    }

    public void setSubtasks(List<SubTask> subtasks) {
        this.subtasks = subtasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_subtask, parent, false);
        return new SubtaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskViewHolder holder, int position) {
        holder.bind(subtasks.get(position));
    }

    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    class SubtaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSubtask;
        EditText etSubtaskTitle;
        ImageView btnDeleteSubtask;
        TextWatcher titleWatcher;

        public SubtaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSubtask = itemView.findViewById(R.id.cbSubtask);
            etSubtaskTitle = itemView.findViewById(R.id.etSubtaskTitle);
            btnDeleteSubtask = itemView.findViewById(R.id.btnDeleteSubtask);
        }

        void bind(SubTask subtask) {
            // Remove previous watcher to avoid multiple calls when recycling
            if (titleWatcher != null) {
                etSubtaskTitle.removeTextChangedListener(titleWatcher);
            }

            etSubtaskTitle.setText(subtask.getTitle());
            cbSubtask.setChecked(subtask.isCompleted());
            updateStrikethrough(subtask.isCompleted());

            cbSubtask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                subtask.setCompleted(isChecked);
                updateStrikethrough(isChecked);
                if (listener != null) {
                    listener.onSubtaskStatusChanged(subtask, isChecked);
                }
            });

            titleWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    String newTitle = s.toString();
                    subtask.setTitle(newTitle);
                    if (listener != null) {
                        listener.onSubtaskTitleChanged(subtask, newTitle);
                    }
                }
            };
            etSubtaskTitle.addTextChangedListener(titleWatcher);

            btnDeleteSubtask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSubtaskDeleted(subtask);
                }
            });
        }

        private void updateStrikethrough(boolean isCompleted) {
            if (isCompleted) {
                etSubtaskTitle.setPaintFlags(etSubtaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                etSubtaskTitle.setAlpha(0.5f);
            } else {
                etSubtaskTitle.setPaintFlags(etSubtaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                etSubtaskTitle.setAlpha(1.0f);
            }
        }
    }
}