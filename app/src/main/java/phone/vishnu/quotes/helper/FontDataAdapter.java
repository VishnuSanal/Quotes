package phone.vishnu.quotes.helper;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.quotes.R;

public class FontDataAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private final ArrayList<String> objects;

    public FontDataAdapter(@NonNull Context context, ArrayList<String> objects) {
        super(context, 0, objects);
        this.objects = objects;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View rootView = convertView;
        final FontDataAdapter.ViewHolder viewHolder;

        if (rootView == null) {
            viewHolder = new FontDataAdapter.ViewHolder();
            rootView = inflater.inflate(R.layout.font_single_item, parent, false);

            rootView.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            viewHolder.fontTV = rootView.findViewById(R.id.quoteTextFontSingleItem);
            viewHolder.progressBar = rootView.findViewById(R.id.singleItemFontProgressBar);

            rootView.setTag(viewHolder);

        } else {
            viewHolder = (FontDataAdapter.ViewHolder) rootView.getTag();
        }
        viewHolder.fontTV.setText(objects.get(position));

        String fontString = objects.get(position).toLowerCase() + ".ttf";

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fonts").child(fontString);

        final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

        final File f = new File(localFile + File.separator + "." + fontString);

        if (f.exists()) {
            viewHolder.progressBar.setProgress(100);

            Typeface face = Typeface.createFromFile(f);
            viewHolder.fontTV.setTypeface(face);
        } else {
            storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Typeface face = Typeface.createFromFile(f);
                    viewHolder.fontTV.setTypeface(face);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                    viewHolder.progressBar.setProgress(
                            (int) ((100.0 * taskSnapshot.getBytesTransferred()) / (taskSnapshot.getTotalByteCount()))
                    );
                }
            });
        }
        return rootView;
    }


    static class ViewHolder {
        TextView fontTV;
        ProgressBar progressBar;
    }
}