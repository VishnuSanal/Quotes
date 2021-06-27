package phone.vishnu.quotes.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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

import phone.vishnu.quotes.R;

public class ColorAdapter extends ListAdapter<String, ColorAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public ColorAdapter() {
        super(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.color_pick_single_item, parent, false);
        return new ColorAdapter.ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        if (getItem(position).equals("#00000000")) {

            holder.imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                            holder.itemView.getContext(),
                            R.drawable.ic_no_color
                    )
            );

            holder.textView.setText("TRANSPARENT");

        } else {

            ShapeDrawable d = new ShapeDrawable(new OvalShape());
            d.getPaint().setStyle(Paint.Style.FILL);
            d.getPaint().setColor(Color.parseColor(getItem(position)));

            holder.imageView.setBackgroundDrawable(
                    d
            );

            holder.textView.setText(getItem(position).replace("#FF", "#"));

        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(String colorString);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.colorPickSingleView);
            textView = itemView.findViewById(R.id.colorPickSingleTV);

            imageView.setOnClickListener(this);
            textView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                listener.onItemClick(getItem(getAdapterPosition()));
        }
    }
}