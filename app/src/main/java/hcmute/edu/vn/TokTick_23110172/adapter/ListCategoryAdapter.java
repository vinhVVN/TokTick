package hcmute.edu.vn.TokTick_23110172.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.EditListActivity;
import hcmute.edu.vn.TokTick_23110172.R;
import hcmute.edu.vn.TokTick_23110172.data.local.entity.ListCategory;

public class ListCategoryAdapter extends RecyclerView.Adapter<ListCategoryAdapter.ViewHolder> {

    private List<ListCategory> categories = new ArrayList<>();
    private final OnListOrderChangedListener orderChangedListener;

    public interface OnListOrderChangedListener {
        void onOrderChanged(List<ListCategory> updatedCategories);
    }

    public ListCategoryAdapter(OnListOrderChangedListener orderChangedListener) {
        this.orderChangedListener = orderChangedListener;
    }

    public void setCategories(List<ListCategory> categories) {
        this.categories = new ArrayList<>(categories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListCategory category = categories.get(position);
        holder.tvName.setText(category.getName());
        holder.tvEmoji.setText(category.getIconName() != null ? category.getIconName() : "📁");

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, EditListActivity.class);
            intent.putExtra("LIST_ID", category.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(categories, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void onDragFinished() {
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setOrderIndex(i);
        }
        if (orderChangedListener != null) {
            orderChangedListener.onOrderChanged(categories);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName;
        ImageView ivDragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvListEmoji);
            tvName = itemView.findViewById(R.id.tvListName);
            ivDragHandle = itemView.findViewById(R.id.ivDragHandle);
        }
    }
}
