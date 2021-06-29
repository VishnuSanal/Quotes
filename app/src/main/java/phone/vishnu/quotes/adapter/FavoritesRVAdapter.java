package phone.vishnu.quotes.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Random;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

public class FavoritesRVAdapter extends ListAdapter<Quote, FavoritesRVAdapter.ViewHolder> {
    public FavoritesRVAdapter() {
        super(new DiffUtil.ItemCallback<Quote>() {
            @Override
            public boolean areItemsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Quote oldItem, @NonNull Quote newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quote quote = getItem(position);

        holder.colorView.setBackground(new ColorDrawable(getCardBGColor()));
        holder.quoteTV.setText(quote.getQuote());
        holder.authorTV.setText(String.format("- %s", quote.getAuthor()));
    }

    private int getCardBGColor() {

        String[] colorArray = new String[]{
                "#e57373",
                "#f06292",
                "#ba68c8",
                "#9575cd",
                "#7986cb",
                "#64b5f6",
                "#4fc3f7",
                "#4dd0e1",
                "#4db6ac",
                "#81c784",
                "#aed581",
                "#dce775",
                "#fff176",
                "#ffd54f",
                "#ffb74d",
                "#ff8a65",
                "#a1887f"
        };

        return Color.parseColor(colorArray[new Random().nextInt(colorArray.length - 1)]);
    }

    public Quote getFav(int position) {
        return getItem(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView quoteTV, authorTV;
        public final View colorView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            quoteTV = itemView.findViewById(R.id.favouriteQuoteTV);
            authorTV = itemView.findViewById(R.id.favouriteAuthorTV);
            colorView = itemView.findViewById(R.id.favouriteSampleColorView);
        }
    }
}
