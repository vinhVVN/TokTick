package hcmute.edu.vn.TokTick_23110172.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hcmute.edu.vn.TokTick_23110172.R
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory

sealed class SidebarItem {
    data class Header(val title: String) : SidebarItem()
    data class MenuItem(val category: ListCategory, val taskCount: Int) : SidebarItem()
}

class SidebarAdapter : ListAdapter<SidebarItem, RecyclerView.ViewHolder>(SidebarDiffCallback()) {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_MENU_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SidebarItem.Header -> TYPE_HEADER
            is SidebarItem.MenuItem -> TYPE_MENU_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.item_sidebar_header, parent, false))
            TYPE_MENU_ITEM -> MenuViewHolder(inflater.inflate(R.layout.item_sidebar_menu, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item as SidebarItem.Header)
            is MenuViewHolder -> holder.bind(item as SidebarItem.MenuItem)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeaderTitle: TextView = itemView.findViewById(R.id.tvSidebarHeader)
        fun bind(header: SidebarItem.Header) {
            tvHeaderTitle.text = header.title
        }
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMenuIcon: ImageView = itemView.findViewById(R.id.ivMenuIcon)
        private val tvMenuTitle: TextView = itemView.findViewById(R.id.tvMenuTitle)
        private val tvTaskCount: TextView = itemView.findViewById(R.id.tvMenuCount)

        fun bind(menuItem: SidebarItem.MenuItem) {
            tvMenuTitle.text = menuItem.category.name
            tvTaskCount.text = if (menuItem.taskCount > 0) menuItem.taskCount.toString() else ""
            
            // Map icon từ resource hoặc dùng mặc định
            val context = itemView.context
            val iconResId = context.resources.getIdentifier(
                menuItem.category.iconName, "drawable", context.packageName
            )
            
            if (iconResId != 0) {
                ivMenuIcon.setImageResource(iconResId)
            } else {
                // Icon mặc định nếu không tìm thấy
                ivMenuIcon.setImageResource(R.drawable.ic_launcher_foreground) 
            }
        }
    }

    class SidebarDiffCallback : DiffUtil.ItemCallback<SidebarItem>() {
        override fun areItemsTheSame(oldItem: SidebarItem, newItem: SidebarItem): Boolean {
            return when {
                oldItem is SidebarItem.Header && newItem is SidebarItem.Header -> 
                    oldItem.title == newItem.title
                oldItem is SidebarItem.MenuItem && newItem is SidebarItem.MenuItem -> 
                    oldItem.category.id == newItem.category.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: SidebarItem, newItem: SidebarItem): Boolean {
            return oldItem == newItem
        }
    }
}
