package phone.vishnu.quotes.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class ColorAdapter extends BaseAdapter {

    private final String[] colors = new String[]{
            "#FFF44336",
            "#FFE91E63",
            "#FF9C27B0",
            "#FF673AB7",
            "#FF3F51B5",
            "#FF2196F3",
            "#FF03A9F4",
            "#FF00BCD4",
            "#FF009688",
            "#FF4CAF50",
            "#FF8BC34A",
            "#FFCDDC39",
            "#FFFFEB3B",
            "#FFFFC107",
            "#FFFF9800",
            "#FFFF5722",
            "#FF795548",
            "#FF9E9E9E",
            "#FF607D8B",
            "#FF000000",
            "#00000000"
    };
    private final Context context;

    public ColorAdapter(Context c) {
        context = c;
    }

    public String getColor(int position) {
        return colors[position];
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    @Nullable
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (convertView == null) ? new ImageView(context) : (ImageView) convertView;

        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.getPaint().setStyle(Paint.Style.FILL);
        d.getPaint().setColor(Color.parseColor(colors[position]));

//        GradientDrawable d = new GradientDrawable();
//        d.setShape(GradientDrawable.OVAL);
//        d.setColor(Color.parseColor(colors[position]));
//        d.setStroke(1, context.getResources().getColor(R.color.spacerColor));

        imageView.setLayoutParams(new GridView.LayoutParams(100, 100));

        imageView.setBackground(d);
//        imageView.setElevation(12);

        return imageView;
    }

}