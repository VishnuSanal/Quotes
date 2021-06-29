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
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.FontLVAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class FontFragment extends BottomSheetDialogFragment {

    private final ArrayList<String> fontList = new ArrayList<>();
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ListView listView;
    private FontLVAdapter fontDataAdapter;
    private LinearProgressIndicator progressBar;

    public FontFragment() {
    }

    public static FontFragment newInstance() {
        return new FontFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font, container, false);
        if (!isNetworkAvailable(requireContext()))
            Toast.makeText(requireContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();

        progressBar = inflate.findViewById(R.id.fontProgressBar);
        listView = inflate.findViewById(R.id.fontListView);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (MainActivity.fontDialog != null && MainActivity.fontDialog.isShowing())
            MainActivity.fontDialog.dismiss();

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
        final File[] files = root.listFiles();

        if (files != null) {
            for (File file : files) {

                if (file.getAbsolutePath().endsWith(".ttf")) {

                    String fontString = file.getName().replace(".ttf", "");

                    fontString = fontString.toUpperCase().charAt(1) + fontString.substring(2);

                    fontList.add(fontString);
                }
            }
            if (getContext() != null)
                if (fontDataAdapter == null) {
                    fontDataAdapter = new FontLVAdapter(Objects.requireNonNull(requireContext()), fontList);
                    listView.setAdapter(fontDataAdapter);
                } else {
                    fontDataAdapter.clear();
                    fontDataAdapter.addAll(fontList);
                    fontDataAdapter.notifyDataSetChanged();
                }
        }

        if (isNetworkAvailable(requireContext())) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("fonts");
            storageRef.listAll().addOnSuccessListener(listResult -> {

                for (StorageReference item : listResult.getItems()) {

                    String fontString = item.getName().replace(".ttf", "");

                    fontString = fontString.toUpperCase().charAt(0) + fontString.substring(1);

                    ArrayList<String> toBeRemoved = sharedPreferenceHelper.getFontListToBeRemoved();

                    if (!fontList.contains(fontString) && !toBeRemoved.contains("." + item.getName().toLowerCase())) {

                        if (getContext() != null && fontDataAdapter != null) {
                            fontDataAdapter.add(fontString);
                            fontDataAdapter.notifyDataSetChanged();
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
            });
        }

        listView.setOnItemClickListener((parent, view1, position, id) -> {

            final ProgressDialog progressDialog = ProgressDialog.show(requireContext(), "", "Please Wait....");

            String fontString = fontList.get(position).toLowerCase() + ".ttf";

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("fonts").child(fontString);
            final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
            final File f = new File(localFile + File.separator + "." + fontString);

            if (f.exists()) {
                sharedPreferenceHelper.setFontPath(f.toString());

                Toast.makeText(requireContext(), "Font Set \n Applying Changes", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

                ((MainActivity) requireContext()).getQuoteViewPagerAdapter().notifyDataSetChanged();

                dismiss();

            } else {

                if (!localFile.exists()) localFile.mkdirs();

                storageReference.getFile(f).addOnSuccessListener(taskSnapshot -> {
                    if (getActivity() != null) {

                        sharedPreferenceHelper.setFontPath(f.toString());

                        Toast.makeText(requireContext(), "Font Set \n Applying Changes", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        ((MainActivity) requireContext()).getQuoteViewPagerAdapter().notifyDataSetChanged();

                        dismiss();

                    } else {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(exception -> {
                    exception.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(exception);
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Oops! Something went wrong!", Toast.LENGTH_LONG).show();
                });
            }

        });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}