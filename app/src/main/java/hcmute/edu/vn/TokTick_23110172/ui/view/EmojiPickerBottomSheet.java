package hcmute.edu.vn.TokTick_23110172.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

import hcmute.edu.vn.TokTick_23110172.R;

public class EmojiPickerBottomSheet extends BottomSheetDialogFragment {

    public interface OnEmojiSelectedListener {
        void onEmojiSelected(String emoji);
    }

    private OnEmojiSelectedListener listener;
    private final List<String> emojis = Arrays.asList(
            "😀", "😃", "😄", "😁", "😆", "😅", "😂", "🤣", "😊", "😇", "🙂", "🙃", "😉", "😌", "😍", "🥰", "😘", "😗", "😙", "😚", "😋", "😛", "😝", "😜", "🤪", "🤨", "🧐", "🤓", "😎", "🤩", "🥳", "😏", "😒", "😞", "😔", "😟", "😕", "🙁", "☹️", "😣", "😖", "😫", "😩", "🥺", "😢", "😭", "😤", "😠", "😡", "🤬", "🤯", "😳", "🥵", "🥶", "😱", "😨", "😰", "😥", "😓", "🤗", "🤔", "🤭", "🤫", "🤥", "😶", "😐", "😑", "😬", "🙄", "😯", "😦", "😧", "😮", "😲", "🥱", "😴", "🤤", "😪", "😵", "🤐", "🥴", "🤢", "🤮", "🤧", "😷", "🤒", "🤕", "🤑", "🤠", "😈", "👿", "👹", "👺", "🤡", "💩", "👻", "💀", "☠️", "👽", "👾", "🤖", "🎃", "😺", "😸", "😻", "😼", "😽", "🙀", "😿", "😾",
            "🏠", "🏡", "🏢", "🏣", "🏤", "🏥", "🏦", "🏨", "🏩", "🏪", "🏫", "🏬", "🏭", "🏯", "🏰", "💒", "🗼", "🗽", "⛪", "🕌", "🕍", "⛩️", "🕋", "⛲", "⛺", "🌁", "🌃", "🏙️", "🌄", "🌅", "🌆", "🌇", "🌉", "♨️", "🌌", "🎠", "🎡", "🎢", "💈", "🎪",
            "💼", "📁", "📂", "📅", "📆", "📇", "📈", "📉", "📊", "📋", "📌", "📍", "📎", "📏", "📐", "✂️", "🗂️", "🗃️", "🗄️", "🗑️",
            "🏋️‍♂️", "🏃‍♂️", "🚶‍♂️", "🚴‍♂️", "🚵‍♂️", "🤸‍♂️", "⛹️‍♂️", "🤾‍♂️", "🏌️‍♂️", "🧘‍♂️", "🏊‍♂️", "🚣‍♂️", "🧗‍♂️", "🏇",
            "📚", "📖", "📜", "📝", "📓", "📔", "📒", "📕", "📗", "📘", "📙", "📚", "📓",
            "✈️", "🚀", "🛸", "🚁", "🛶", "⛵", "🚢", "🛳️", "⛴️", "🛥️", "🛳️", "⛴️", "🛥️", "🚢"
    );

    public void setOnEmojiSelectedListener(OnEmojiSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emoji_picker_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnCloseEmoji).setOnClickListener(v -> dismiss());

        RecyclerView rvEmojis = view.findViewById(R.id.rvEmojis);
        rvEmojis.setLayoutManager(new GridLayoutManager(getContext(), 7));
        rvEmojis.setAdapter(new EmojiAdapter());
    }

    private class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String emoji = emojis.get(position);
            ((TextView) holder.itemView).setText(emoji);
            ((TextView) holder.itemView).setTextSize(24);
            ((TextView) holder.itemView).setGravity(android.view.Gravity.CENTER);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEmojiSelected(emoji);
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return emojis.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}
