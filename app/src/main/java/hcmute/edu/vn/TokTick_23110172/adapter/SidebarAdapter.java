package hcmute.edu.vn.TokTick_23110172.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import hcmute.edu.vn.TokTick_23110172.R;

public class SidebarAdapter extends ListAdapter<SidebarItem, RecyclerView.ViewHolder> {

    private final OnSidebarItemClickListener listener;
    private int selectedItemId = SidebarItem.ID_TODAY;

    public interface OnSidebarItemClickListener {
        void onSmartFilterClick(int smartFilterId);
        void onUserListClick(int listCategoryId);
    }

    public SidebarAdapter(OnSidebarItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == SidebarItem.TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_sidebar_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_sidebar_menu, parent, false);
            return new MenuViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SidebarItem item = getItem(position);
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item);
        } else if (holder instanceof MenuViewHolder) {
            boolean isSelected = false;
            if (item.type == SidebarItem.TYPE_SMART_FILTER) {
                isSelected = (item.smartFilterId == selectedItemId);
            } else if (item.type == SidebarItem.TYPE_USER_LIST) {
                isSelected = (item.userList != null && item.userList.getId() == selectedItemId);
            }
            ((MenuViewHolder) holder).bind(item, isSelected, listener);
        }
    }

    public void setSelectedItemId(int id) {
        this.selectedItemId = id;
        notifyDataSetChanged();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvSidebarHeader);
        }

        void bind(SidebarItem item) {
            tvHeader.setText(item.headerText);
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvEmoji;
        TextView tvTitle;
        TextView tvCount;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivMenuIcon);
            tvEmoji = itemView.findViewById(R.id.tvMenuEmoji);
            tvTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvCount = itemView.findViewById(R.id.tvMenuCount);
        }

        void bind(SidebarItem item, boolean isSelected, OnSidebarItemClickListener listener) {
            Context context = itemView.getContext();
            itemView.setActivated(isSelected);

            String iconName = "";
            if (item.type == SidebarItem.TYPE_SMART_FILTER) {
                tvTitle.setText(item.smartFilterName);
                iconName = item.smartFilterIcon;
            } else if (item.type == SidebarItem.TYPE_USER_LIST && item.userList != null) {
                tvTitle.setText(item.userList.getName());
                iconName = item.userList.getIconName();
            }
            
            tvCount.setText(String.valueOf(item.taskCount));

            // Logic xử lý Icon hoặc Emoji
            if (iconName == null || iconName.isEmpty()) {
                ivIcon.setVisibility(View.VISIBLE);
                tvEmoji.setVisibility(View.GONE);
                ivIcon.setImageResource(R.drawable.ic_notes);
            } else if (isEmoji(iconName)) {
                ivIcon.setVisibility(View.GONE);
                tvEmoji.setVisibility(View.VISIBLE);
                tvEmoji.setText(iconName);
            } else {
                int iconResId = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
                if (iconResId != 0) {
                    ivIcon.setVisibility(View.VISIBLE);
                    tvEmoji.setVisibility(View.GONE);
                    ivIcon.setImageResource(iconResId);
                } else {
                    ivIcon.setVisibility(View.VISIBLE);
                    tvEmoji.setVisibility(View.GONE);
                    ivIcon.setImageResource(R.drawable.ic_notes);
                }
            }

            // Cập nhật màu sắc dựa trên trạng thái chọn
            int mainTextColor = isSelected ? ContextCompat.getColor(context, R.color.sidebar_item_activated_text) : ContextCompat.getColor(context, R.color.text_main_light);
            int mutedTextColor = isSelected ? ContextCompat.getColor(context, R.color.sidebar_item_activated_text) : ContextCompat.getColor(context, R.color.text_muted_light);

            tvTitle.setTextColor(mainTextColor);
            tvCount.setTextColor(mutedTextColor);
            ivIcon.setImageTintList(ColorStateList.valueOf(mutedTextColor));

            itemView.setOnClickListener(v -> {
                int id;
                if (item.type == SidebarItem.TYPE_SMART_FILTER) {
                    id = item.smartFilterId;
                    listener.onSmartFilterClick(id);
                } else {
                    id = item.userList.getId();
                    listener.onUserListClick(id);
                }
                selectedItemId = id;
                notifyDataSetChanged();
            });
        }

        private boolean isEmoji(String str) {
            if (str == null || str.isEmpty()) return false;
            // Kiểm tra đơn giản: nếu không phải tên resource (thường bắt đầu bằng ic_) 
            // và chứa ký tự đặc biệt hoặc độ dài ngắn thì coi là emoji/text
            return !str.startsWith("ic_") && str.length() <= 4; 
        }
    }

    private static final DiffUtil.ItemCallback<SidebarItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<SidebarItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull SidebarItem oldItem, @NonNull SidebarItem newItem) {
            if (oldItem.type != newItem.type) return false;
            if (oldItem.type == SidebarItem.TYPE_SMART_FILTER) {
                return oldItem.smartFilterId == newItem.smartFilterId;
            } else if (oldItem.type == SidebarItem.TYPE_USER_LIST) {
                if (oldItem.userList == null || newItem.userList == null) return false;
                return oldItem.userList.getId() == newItem.userList.getId();
            } else {
                return Objects.equals(oldItem.headerText, newItem.headerText);
            }
        }

        @Override
        public boolean areContentsTheSame(@NonNull SidebarItem oldItem, @NonNull SidebarItem newItem) {
            if (oldItem.type != newItem.type) return false;
            if (oldItem.taskCount != newItem.taskCount) return false;

            if (oldItem.type == SidebarItem.TYPE_USER_LIST) {
                if (oldItem.userList == null || newItem.userList == null) return false;
                return Objects.equals(oldItem.userList.getName(), newItem.userList.getName()) &&
                       Objects.equals(oldItem.userList.getIconName(), newItem.userList.getIconName());
            } else if (oldItem.type == SidebarItem.TYPE_SMART_FILTER) {
                return Objects.equals(oldItem.smartFilterName, newItem.smartFilterName) &&
                       Objects.equals(oldItem.smartFilterIcon, newItem.smartFilterIcon);
            }
            return true;
        }
    };
}
