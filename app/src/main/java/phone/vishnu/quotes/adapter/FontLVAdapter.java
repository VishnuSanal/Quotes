package phone.vishnu.quotes.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.quotes.R;

public class FontLVAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private final ArrayList<String> objects;

    public FontLVAdapter(@NonNull Context context, ArrayList<String> objects) {
        super(context, 0, objects);
        this.objects = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, final View convertView, @NonNull ViewGroup parent) {

        View rootView = convertView;
        final FontLVAdapter.ViewHolder viewHolder;

        if (rootView == null) {
            viewHolder = new FontLVAdapter.ViewHolder();
            rootView = inflater.inflate(R.layout.font_single_item, parent, false);

            viewHolder.fontTV = rootView.findViewById(R.id.quoteTextFontSingleItem);
            viewHolder.progressBar = rootView.findViewById(R.id.singleItemFontProgressBar);

            rootView.setTag(viewHolder);

            String fontString = objects.get(position).toLowerCase() + ".ttf";

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fonts").child(fontString);

            final File f = new File(getContext().getFilesDir(), fontString);

            if (f.exists()) {

                viewHolder.progressBar.setVisibility(View.GONE);
                try {
                    Typeface face = Typeface.createFromFile(f);
                    viewHolder.fontTV.setTypeface(face);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            } else {

                storageReference.getFile(f).addOnSuccessListener(taskSnapshot -> {

                    viewHolder.progressBar.setVisibility(View.GONE);

                    try {
                        Typeface face = Typeface.createFromFile(f);
                        viewHolder.fontTV.setTypeface(face);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Oops! Something went wrong", Toast.LENGTH_SHORT).show();
                        FirebaseCrashlytics.getInstance().recordException(e);
                        e.printStackTrace();
                    }

                }).addOnFailureListener(exception -> {
                    FirebaseCrashlytics.getInstance().recordException(exception);
                    exception.printStackTrace();
                });
            }
        } else {
            viewHolder = (FontLVAdapter.ViewHolder) rootView.getTag();
        }

        String fontString = objects.get(position).replace(".ttf", "");

        fontString = fontString.toUpperCase().charAt(0) + fontString.substring(1);

        viewHolder.fontTV.setText(fontString);

        return rootView;
    }

    static class ViewHolder {
        TextView fontTV;
        LinearProgressIndicator progressBar;
    }
}