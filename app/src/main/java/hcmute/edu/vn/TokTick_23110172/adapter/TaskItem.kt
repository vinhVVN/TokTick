package hcmute.edu.vn.TokTick_23110172.adapter

import hcmute.edu.vn.TokTick_23110172.data.local.entity.Task

sealed class TaskItem {
    data class Header(val title: String, val count: Int) : TaskItem()
    data class TaskData(val task: Task) : TaskItem()
}
