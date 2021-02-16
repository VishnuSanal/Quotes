package phone.vishnu.quotes.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.FontDataAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class FontFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;
    private ListView listView;
    private FontDataAdapter fontDataAdapter;
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

        final String fontArrayListString = sharedPreferenceHelper.getFontArrayString();

        if (null != fontArrayListString) {
            fontList = new ArrayList<>();

            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            fontList = gson.fromJson(fontArrayListString, type);

//            progressBar.setVisibility(View.GONE);
            if (getContext() != null)
                if (fontDataAdapter == null) {
                    fontDataAdapter = new FontDataAdapter(Objects.requireNonNull(requireContext()), fontList);
                    listView.setAdapter(fontDataAdapter);
                } else {
                    fontDataAdapter.clear();
                    fontDataAdapter.addAll(fontList);
                    fontDataAdapter.notifyDataSetChanged();
                }
        }

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

                Gson gson = new Gson();

                sharedPreferenceHelper.setFontArrayString(gson.toJson(fontList));

                if (getContext() != null)
                    if (fontDataAdapter == null) {
                        fontDataAdapter = new FontDataAdapter(Objects.requireNonNull(requireContext()), fontList);
                        listView.setAdapter(fontDataAdapter);
                    } else {
                        fontDataAdapter.clear();
                        fontDataAdapter.addAll(fontList);
                        fontDataAdapter.notifyDataSetChanged();
                    }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final ProgressDialog progressDialog = ProgressDialog.show(requireContext(), "", "Please Wait....");

                String fontString = fontList.get(position).toLowerCase() + ".ttf";

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fonts").child(fontString);
                final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");
                final File f = new File(localFile + File.separator + "." + fontString);

                if (f.exists()) {
                    sharedPreferenceHelper.setFontPath(f.toString());

                    Toast.makeText(requireContext(), "Font Set..... \n Applying Changes", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    ((MainActivity) requireContext()).getQuoteViewPagerAdapter().notifyDataSetChanged();

                    requireActivity().onBackPressed();
                } else {

                    if (!localFile.exists()) localFile.mkdirs();

                    storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            if (getActivity() != null) {

                                sharedPreferenceHelper.setFontPath(f.toString());

                                Toast.makeText(requireContext(), "Font Set..... \n Applying Changes", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                ((MainActivity) requireContext()).getQuoteViewPagerAdapter().notifyDataSetChanged();

                                requireActivity().onBackPressed();
                            } else {
                                progressDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(exception);
                            progressDialog.dismiss();
                            Toast.makeText(requireContext(), "Error.....", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font, container, false);
        if (!isNetworkAvailable())
            Toast.makeText(requireContext(), "Please Connect to the Internet...", Toast.LENGTH_SHORT).show();

        progressBar = inflate.findViewById(R.id.fontProgressBar);
        listView = inflate.findViewById(R.id.fontListView);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        return inflate;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}