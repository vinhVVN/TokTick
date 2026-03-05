package hcmute.edu.vn.TokTick_23110172.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task;

public class TaskAdapter extends ListAdapter<TaskItem, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TASK = 1;

    private final Map<String, Boolean> expandedStates = new HashMap<>();
    private List<Task> lastRawTasks = new ArrayList<>();

    public TaskAdapter() {
        super(new TaskDiffCallback());
    }

    @Override
    public int getItemViewType(int position) {
        TaskItem item = getItem(position);
        if (item instanceof TaskItem.Header) {
            return TYPE_HEADER;
        } else {
            return TYPE_TASK;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TaskItem item = getItem(position);
        if (holder instanceof HeaderViewHolder && item instanceof TaskItem.Header) {
            ((HeaderViewHolder) holder).bind((TaskItem.Header) item);
        } else if (holder instanceof TaskViewHolder && item instanceof TaskItem.TaskData) {
            ((TaskViewHolder) holder).bind(((TaskItem.TaskData) item).getTask());
        }
    }

    public void submitTaskList(List<Task> tasks) {
        this.lastRawTasks = tasks;
        List<TaskItem> items = new ArrayList<>();

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);

        List<Task> sortedTasks = new ArrayList<>(tasks);
        Collections.sort(sortedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                Long d1 = t1.getDueDate() != null ? t1.getDueDate() : Long.MAX_VALUE;
                Long d2 = t2.getDueDate() != null ? t2.getDueDate() : Long.MAX_VALUE;
                return d1.compareTo(d2);
            }
        });

        Map<String, List<Task>> groupedTasks = new LinkedHashMap<>();
        for (Task task : sortedTasks) {
            String headerTitle = task.getDueDate() != null ? getFormattedDate(task.getDueDate(), todayCalendar) : "Unscheduled";
            if (!groupedTasks.containsKey(headerTitle)) {
                groupedTasks.put(headerTitle, new ArrayList<>());
            }
            groupedTasks.get(headerTitle).add(task);
        }

        for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
            String headerTitle = entry.getKey();
            List<Task> taskGroup = entry.getValue();
            
            Boolean isExpanded = expandedStates.get(headerTitle);
            if (isExpanded == null) isExpanded = true;
            
            items.add(new TaskItem.Header(headerTitle, taskGroup.size(), isExpanded));

            if (isExpanded) {
                for (Task task : taskGroup) {
                    items.add(new TaskItem.TaskData(task));
                }
            }
        }
        submitList(items);
    }

    private String getFormattedDate(long timestamp, Calendar today) {
        Calendar taskDate = Calendar.getInstance();
        taskDate.setTimeInMillis(timestamp);
        taskDate.set(Calendar.HOUR_OF_DAY, 0);
        taskDate.set(Calendar.MINUTE, 0);
        taskDate.set(Calendar.SECOND, 0);
        taskDate.set(Calendar.MILLISECOND, 0);

        long diffMillis = taskDate.getTimeInMillis() - today.getTimeInMillis();
        int diffDays = (int) (diffMillis / (24 * 60 * 60 * 1000));

        if (diffDays < 0) return "Overdue";
        if (diffDays == 0) return "Today";
        if (diffDays == 1) return "Tomorrow";
        if (diffDays >= 2 && diffDays <= 7) return "Next 7 Days";
        
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHeaderTitle;
        private final TextView tvTaskCount;
        private final ImageView ivExpandIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderTitle = itemView.findViewById(R.id.tvHeaderTitle);
            tvTaskCount = itemView.findViewById(R.id.tvTaskCount);
            ivExpandIcon = itemView.findViewById(R.id.ivExpand);
        }

        public void bind(final TaskItem.Header header) {
            tvHeaderTitle.setText(header.getTitle());
            tvTaskCount.setText(String.valueOf(header.getCount()));

            ivExpandIcon.animate().rotation(header.isExpanded() ? 0f : -90f).setDuration(200).start();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean newState = !header.isExpanded();
                    expandedStates.put(header.getTitle(), newState);
                    submitTaskList(lastRawTasks);
                }
            });
        }
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvTime;
        private final CheckBox cbTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTime = itemView.findViewById(R.id.tvTaskTime);
            cbTask = itemView.findViewById(R.id.cbTask);
        }

        public void bind(Task task) {
            tvTitle.setText(task.getTitle());
            tvTime.setText(task.getDueTime() != null ? task.getDueTime() : "");
            cbTask.setChecked(task.isCompleted());
        }
    }

    static class TaskDiffCallback extends DiffUtil.ItemCallback<TaskItem> {
        @Override
        public boolean areItemsTheSame(@NonNull TaskItem oldItem, @NonNull TaskItem newItem) {
            if (oldItem instanceof TaskItem.Header && newItem instanceof TaskItem.Header) {
                return ((TaskItem.Header) oldItem).getTitle().equals(((TaskItem.Header) newItem).getTitle());
            } else if (oldItem instanceof TaskItem.TaskData && newItem instanceof TaskItem.TaskData) {
                return ((TaskItem.TaskData) oldItem).getTask().getId() == ((TaskItem.TaskData) newItem).getTask().getId();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaskItem oldItem, @NonNull TaskItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
