package phone.vishnu.quotes.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

public class FavoritesDataAdapter extends ArrayAdapter<Quote> {

    private final LayoutInflater inflater;
    private final View.OnClickListener viewImageViewOnClickListener;
    private final View.OnClickListener removeImageViewOnClickListener;
    private final ArrayList<Quote> objects;

    public FavoritesDataAdapter(@NonNull Context context, ArrayList<Quote> objects, View.OnClickListener viewImageViewOnClickListener, View.OnClickListener removeImageViewOnClickListener) {
        super(context, 0, objects);
        this.objects = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.viewImageViewOnClickListener = viewImageViewOnClickListener;
        this.removeImageViewOnClickListener = removeImageViewOnClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

//        Collections.reverse(objects);
        Quote item = objects.get(position);
        View rootView = convertView;
        final ViewHolder viewHolder;

        if (rootView == null) {
            viewHolder = new ViewHolder();
            rootView = inflater.inflate(R.layout.favorite_single_item, parent, false);

            viewHolder.quoteTV = rootView.findViewById(R.id.quoteTextSingleItem);
            viewHolder.authorTV = rootView.findViewById(R.id.authorTextSingleItem);
            viewHolder.viewIV = rootView.findViewById(R.id.singleItemShareImageView);
            viewHolder.viewIV.setColorFilter(Color.parseColor("#9C9CFF"));
            viewHolder.removeIV = rootView.findViewById(R.id.singleItemRemoveImageView);

            viewHolder.viewIV.setOnClickListener(viewImageViewOnClickListener);
            viewHolder.removeIV.setOnClickListener(removeImageViewOnClickListener);
            rootView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) rootView.getTag();
        }
        viewHolder.quoteTV.setText(item.getQuote());
        viewHolder.authorTV.setText(item.getAuthor());

        viewHolder.removeIV.setTag(position);
        viewHolder.viewIV.setTag(position);

        return rootView;
    }

    static class ViewHolder {
        TextView quoteTV;
        TextView authorTV;
        ImageView viewIV;
        ImageView removeIV;
    }
}