package phone.vishnu.quotes.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;

public class FontFragment extends Fragment {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> fontList = new ArrayList<>();
    private ProgressBar progressBar;

    public FontFragment() {

    }

    public static FontFragment newInstance() {
        return new FontFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MainActivity.fontDialog != null && MainActivity.fontDialog.isShowing())
            MainActivity.fontDialog.dismiss();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("fonts");
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                fontList = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {

                    String fontString = item.getName().replace(".ttf", "");

                    fontString = fontString.toUpperCase().charAt(0) + fontString.substring(1);

                    fontList.add(fontString);
                }
                progressBar.setVisibility(View.GONE);
                if (getActivity() != null)
                    if (arrayAdapter == null) {
                        arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, fontList);
                        listView.setAdapter(arrayAdapter);
                    } else {
                        arrayAdapter.clear();
                        arrayAdapter.addAll(fontList);
                        arrayAdapter.notifyDataSetChanged();
                    }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Please Wait....");

                String fontString = fontList.get(position).toLowerCase() + ".ttf";

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fonts").child(fontString);

                final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

                final File f = new File(localFile + File.separator + ".font" + ".ttf");

                storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        if (getActivity() != null) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", Context.MODE_PRIVATE).edit();
                            String FONT_PREFERENCE_NAME = "fontPreference";
                            editor.putString(FONT_PREFERENCE_NAME, f.toString());
                            editor.apply();

                            Toast.makeText(getActivity(), "Font Set..... \n Applying Changes", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            ((MainActivity) getActivity()).getQuoteViewPagerAdapter().notifyDataSetChanged();

                            getActivity().onBackPressed();
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        //TODO: Check this
                        FirebaseCrashlytics.getInstance().recordException(exception);
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Error.....", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font, container, false);
        if (!isNetworkAvailable())
            Toast.makeText(getActivity(), "Please Connect to the Internet...", Toast.LENGTH_SHORT).show();

        progressBar = inflate.findViewById(R.id.fontProgressBar);
        listView = inflate.findViewById(R.id.fontListView);
        return inflate;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}