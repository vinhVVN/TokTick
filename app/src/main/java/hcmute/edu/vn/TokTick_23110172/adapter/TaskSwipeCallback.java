package hcmute.edu.vn.TokTick_23110172.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Callback class to handle Swipe actions (Delete and Complete) for Task RecyclerView.
 */
public class TaskSwipeCallback extends ItemTouchHelper.SimpleCallback {

    public interface SwipeListener {
        void onSwipeToTick(int position);
        void onSwipeToDelete(int position);
    }

    private final SwipeListener listener;
    private final Drawable iconCheck;
    private final Drawable iconDelete;
    private final Paint paint = new Paint();

    public TaskSwipeCallback(Context context, SwipeListener listener) {
        // Support Left to Right (RIGHT) and Right to Left (LEFT) swipes
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listener = listener;

        // Load icons and tint them white. 
        // Using standard Android drawables as fallback; ideally, replace with custom assets.
        Drawable check = ContextCompat.getDrawable(context, android.R.drawable.checkbox_on_background);
        Drawable delete = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);

        this.iconCheck = prepareDrawable(check);
        this.iconDelete = prepareDrawable(delete);
    }

    private Drawable prepareDrawable(Drawable drawable) {
        if (drawable == null) return null;
        Drawable wrapped = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapped, Color.WHITE);
        return wrapped;
    }

    @Override
    public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        // Only allow swiping for Task items, skip Headers
        if (!(viewHolder instanceof TaskAdapter.TaskViewHolder)) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Drag and drop not supported
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT) {
            listener.onSwipeToTick(position);
        } else if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipeToDelete(position);
        }
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        float translationX = viewHolder.itemView.getTranslationX();
        // Right swipe (Delete) requires 60% displacement
        if (translationX > 0) {
            return 0.6f;
        }
        // Left swipe (Tick) requires 40% displacement
        return 0.4f;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                           @NonNull RecyclerView.ViewHolder viewHolder,
                           float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();

            if (dX > 0) { // Swiping Right (Delete)
                // Draw Red background
                paint.setColor(Color.parseColor("#F44336"));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                c.drawRect(background, paint);

                if (iconDelete != null) {
                    int intrinsicWidth = iconDelete.getIntrinsicWidth();
                    int intrinsicHeight = iconDelete.getIntrinsicHeight();

                    int iconTop = itemView.getTop() + (int) (height - intrinsicHeight) / 2;
                    int iconMargin = (int) (height - intrinsicHeight) / 2;
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + intrinsicWidth;
                    int iconBottom = iconTop + intrinsicHeight;

                    // Scale effect based on drag distance
                    float scale = Math.min(1.0f, dX / (float) (iconMargin * 2 + intrinsicWidth));
                    c.save();
                    c.scale(scale, scale, iconLeft + intrinsicWidth / 2f, iconTop + intrinsicHeight / 2f);
                    iconDelete.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    iconDelete.draw(c);
                    c.restore();
                }
            } else if (dX < 0) { // Swiping Left (Tick)
                // Draw Green background
                paint.setColor(Color.parseColor("#4CAF50"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, paint);

                if (iconCheck != null) {
                    int intrinsicWidth = iconCheck.getIntrinsicWidth();
                    int intrinsicHeight = iconCheck.getIntrinsicHeight();

                    int iconTop = itemView.getTop() + (int) (height - intrinsicHeight) / 2;
                    int iconMargin = (int) (height - intrinsicHeight) / 2;
                    int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
                    int iconRight = itemView.getRight() - iconMargin;
                    int iconBottom = iconTop + intrinsicHeight;

                    // Scale effect based on drag distance (dX is negative)
                    float scale = Math.min(1.0f, Math.abs(dX) / (float) (iconMargin * 2 + intrinsicWidth));
                    c.save();
                    c.scale(scale, scale, iconLeft + intrinsicWidth / 2f, iconTop + intrinsicHeight / 2f);
                    iconCheck.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    iconCheck.draw(c);
                    c.restore();
                }
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
