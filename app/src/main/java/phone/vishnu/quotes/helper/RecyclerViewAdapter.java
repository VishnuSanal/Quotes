package phone.vishnu.quotes.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private ArrayList<Uri> arr;
    private final Context context;
    private ImageView imageView;

    public RecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public RecyclerViewAdapter(Context context, ArrayList<Uri> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.setIsRecyclable(false);

        Picasso.get()
                .load(arr.get(position))
                .into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog dialog = ProgressDialog.show(context, "", "Please Wait....");

                String[] split = String.valueOf(arr.get(position)).split("%2F")[1].split("\\?");

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(split[0]);

                final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

                final File f = new File(localFile + File.separator + ".Quotes_Background" + ".jpg");

                storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        SharedPreferences.Editor editor = context.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", Context.MODE_PRIVATE).edit();
                        String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
                        editor.putString(BACKGROUND_PREFERENCE_NAME, f.toString());
                        editor.apply();
                        dialog.dismiss();

                        Toast.makeText(context, "Background Set..... \n Applying Changes", Toast.LENGTH_LONG).show();

//                        context.startActivity(new Intent(context, MainActivity.class));
                        ((MainActivity) context).findViewById(R.id.constraintLayout).setBackground(Drawable.createFromPath(f.toString()));
                        ((MainActivity) context).onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        Toast.makeText(context, "Error.....", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
