package hcmute.edu.vn.TokTick_23110172.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;

public class SidebarAdapter extends ListAdapter<SidebarItem, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_MENU_ITEM = 1;

    public SidebarAdapter() {
        super(new SidebarDiffCallback());
    }

    @Override
    public int getItemViewType(int position) {
        SidebarItem item = getItem(position);
        if (item instanceof SidebarItem.Header) {
            return TYPE_HEADER;
        } else if (item instanceof SidebarItem.MenuItem) {
            return TYPE_MENU_ITEM;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            return new HeaderViewHolder(inflater.inflate(R.layout.item_sidebar_header, parent, false));
        } else {
            return new MenuViewHolder(inflater.inflate(R.layout.item_sidebar_menu, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SidebarItem item = getItem(position);
        if (holder instanceof HeaderViewHolder && item instanceof SidebarItem.Header) {
            ((HeaderViewHolder) holder).bind((SidebarItem.Header) item);
        } else if (holder instanceof MenuViewHolder && item instanceof SidebarItem.MenuItem) {
            ((MenuViewHolder) holder).bind((SidebarItem.MenuItem) item);
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvHeaderTitle;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderTitle = itemView.findViewById(R.id.tvSidebarHeader);
        }

        public void bind(SidebarItem.Header header) {
            tvHeaderTitle.setText(header.getTitle());
        }
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivMenuIcon;
        private final TextView tvMenuTitle;
        private final TextView tvTaskCount;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMenuIcon = itemView.findViewById(R.id.ivMenuIcon);
            tvMenuTitle = itemView.findViewById(R.id.tvMenuTitle);
            tvTaskCount = itemView.findViewById(R.id.tvMenuCount);
        }

        public void bind(SidebarItem.MenuItem menuItem) {
            ListCategory category = menuItem.getCategory();
            tvMenuTitle.setText(category.getName());
            tvTaskCount.setText(menuItem.getTaskCount() > 0 ? String.valueOf(menuItem.getTaskCount()) : "");

            Context context = itemView.getContext();
            if (category.getIconName() != null) {
                int iconResId = context.getResources().getIdentifier(
                        category.getIconName(), "drawable", context.getPackageName()
                );

                if (iconResId != 0) {
                    ivMenuIcon.setImageResource(iconResId);
                } else {
                    ivMenuIcon.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                ivMenuIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }
    }

    static class SidebarDiffCallback extends DiffUtil.ItemCallback<SidebarItem> {
        @Override
        public boolean areItemsTheSame(@NonNull SidebarItem oldItem, @NonNull SidebarItem newItem) {
            if (oldItem instanceof SidebarItem.Header && newItem instanceof SidebarItem.Header) {
                return ((SidebarItem.Header) oldItem).getTitle().equals(((SidebarItem.Header) newItem).getTitle());
            } else if (oldItem instanceof SidebarItem.MenuItem && newItem instanceof SidebarItem.MenuItem) {
                return ((SidebarItem.MenuItem) oldItem).getCategory().getId() == ((SidebarItem.MenuItem) newItem).getCategory().getId();
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SidebarItem oldItem, @NonNull SidebarItem newItem) {
            return oldItem.equals(newItem);
        }
    }
}
