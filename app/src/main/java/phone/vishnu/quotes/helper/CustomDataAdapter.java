package phone.vishnu.quotes.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

public class CustomDataAdapter extends ArrayAdapter<Quote> {

    Context _context;
    LayoutInflater inflater;
    private ArrayList<Quote> objects;

    public CustomDataAdapter(Context context, ArrayList<Quote> objects) {
        super(context, 0, objects);
        this.objects = objects;
        _context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = this.getItemViewType(position);
        Quote item = objects.get(position);
        //  convertView = null;
        View rootView = convertView;
        final ViewHolder viewHolder;

        if (rootView == null) {
            viewHolder = new ViewHolder();
            rootView = inflater.inflate(R.layout.single_item, parent, false);

            viewHolder.Roll_No = (TextView) rootView.findViewById(R.id.quoteTextSingleItem);
            viewHolder.Stu_Name = (TextView) rootView.findViewById(R.id.authorTextSingleItem);

            rootView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) rootView.getTag();
        }
        viewHolder.Roll_No.setText(item.getQuote());
        viewHolder.Stu_Name.setText(item.getAuthor());

        return rootView;
    }

    static class ViewHolder {
        TextView Roll_No;
        TextView Stu_Name;
    }
}