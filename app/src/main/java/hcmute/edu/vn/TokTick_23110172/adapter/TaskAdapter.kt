package hcmute.edu.vn.TokTick_23110172.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.TokTick_23110172.R
import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter : ListAdapter<TaskItem, RecyclerView.ViewHolder>(TaskDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_TASK = 1
    }

    private val expandedStates = mutableMapOf<String, Boolean>()
    private var lastRawTasks: List<Task> = emptyList()

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is TaskItem.Header -> TYPE_HEADER
            is TaskItem.TaskData -> TYPE_TASK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_date_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            TaskViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when {
            holder is HeaderViewHolder && item is TaskItem.Header -> holder.bind(item)
            holder is TaskViewHolder && item is TaskItem.TaskData -> holder.bind(item.task)
        }
    }

    fun submitTaskList(tasks: List<Task>) {
        this.lastRawTasks = tasks
        val items = mutableListOf<TaskItem>()
        
        val todayCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val groupedTasks = tasks.sortedBy { it.dueDate ?: Long.MAX_VALUE }
            .groupBy { task ->
                task.dueDate?.let { getFormattedDate(it, todayCalendar) } ?: "Unscheduled"
            }

        for ((headerTitle, taskGroup) in groupedTasks) {
            val isExpanded = expandedStates[headerTitle] ?: true
            items.add(TaskItem.Header(headerTitle, taskGroup.size, isExpanded))

            if (isExpanded) {
                items.addAll(taskGroup.map { TaskItem.TaskData(it) })
            }
        }
        submitList(items)
    }

    private fun getFormattedDate(timestamp: Long, today: Calendar): String {
        val taskDate = Calendar.getInstance().apply { 
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = taskDate.timeInMillis - today.timeInMillis
        val diffDays = (diffMillis / (24 * 60 * 60 * 1000)).toInt()

        return when {
            diffDays < 0 -> "Overdue"
            diffDays == 0 -> "Today"
            diffDays == 1 -> "Tomorrow"
            diffDays in 2..7 -> "Next 7 Days"
            else -> SimpleDateFormat("EEE, dd MMM", Locale.getDefault()).format(Date(timestamp))
        }
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeaderTitle: TextView = itemView.findViewById(R.id.tvHeaderTitle)
        private val tvTaskCount: TextView = itemView.findViewById(R.id.tvTaskCount)
        private val ivExpandIcon: ImageView = itemView.findViewById(R.id.ivExpand) // Đã sửa ID thành ivExpand

        fun bind(header: TaskItem.Header) {
            tvHeaderTitle.text = header.title
            tvTaskCount.text = header.count.toString()

            // Xoay icon mượt mà dựa trên trạng thái đóng/mở
            ivExpandIcon.animate().rotation(if (header.isExpanded) 0f else -90f).setDuration(200).start()

            itemView.setOnClickListener {
                val newState = !header.isExpanded
                expandedStates[header.title] = newState
                submitTaskList(lastRawTasks)
            }
        }
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTaskTime)
        private val cbTask: CheckBox = itemView.findViewById(R.id.cbTask)

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvTime.text = task.dueTime ?: ""
            cbTask.isChecked = task.isCompleted
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return if (oldItem is TaskItem.Header && newItem is TaskItem.Header) {
                oldItem.title == newItem.title
            } else if (oldItem is TaskItem.TaskData && newItem is TaskItem.TaskData) {
                oldItem.task.id == newItem.task.id
            } else false
        }
        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem == newItem
        }
    }
}
