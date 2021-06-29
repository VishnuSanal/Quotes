package phone.vishnu.quotes.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.TourItem;

public class AboutRVAdapter extends ListAdapter<TourItem, AboutRVAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public AboutRVAdapter() {
        super(new DiffUtil.ItemCallback<TourItem>() {
            @Override
            public boolean areItemsTheSame(@NonNull TourItem oldItem, @NonNull TourItem newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull TourItem oldItem, @NonNull TourItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TourItem item = getItem(position);

        holder.iconIV.setImageResource(item.getImgId());
        holder.iconIV.setBackgroundTintList(ColorStateList.valueOf(getColor(position)));

        holder.titleTV.setText(item.getTitle());
        holder.descTV.setText(item.getDesc());
    }

    private int getColor(int position) {

        String[] colorArray = {
                "#D0FF1744",
                "#D0FFD600",
                "#D07C4DFF",
                "#D02979FF",
                "#D000C853",
                "#D01DE9B6",
                "#D0FF5722"
        };

        return Color.parseColor(colorArray[position % colorArray.length]);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconIV;
        private final TextView titleTV;
        private final TextView descTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            iconIV = itemView.findViewById(R.id.aboutSingleItemIconIV);
            titleTV = itemView.findViewById(R.id.aboutSingleItemTitleTV);
            descTV = itemView.findViewById(R.id.aboutSingleItemDescriptionTV);

            itemView.findViewById(R.id.aboutConstraintLayout).setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                    listener.onItemClick(getAdapterPosition());
            });

        }
    }
}