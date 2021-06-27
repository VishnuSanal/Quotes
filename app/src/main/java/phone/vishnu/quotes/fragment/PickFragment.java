package phone.vishnu.quotes.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.RecyclerViewAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class PickFragment extends BottomSheetDialogFragment {

    private RecyclerViewAdapter adapter;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public PickFragment() {
    }

    public static PickFragment newInstance() {
        return new PickFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pick, container, false);
        setUpRecyclerView(inflate);
        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        if (!isNetworkAvailable(requireContext()))
            Toast.makeText(requireContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.setOnItemClickListener(uri -> {
            final ProgressDialog dialog = ProgressDialog.show(requireContext(), "", "Please Wait....");

            final File localFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));

            final File f;

            if (uri.toString().contains((BuildConfig.DEBUG) ? "https://firebasestorage.googleapis.com/v0/b/quotes-debug-q.appspot.com" : "https://firebasestorage.googleapis.com/v0/b/quotes-q.appspot.com")) {
                String fileName = String.valueOf(uri).split("%2F")[1].split("\\?")[0];

                f = new File(localFile + File.separator + "." + fileName);

            } else {
                f = new File(uri.getPath());
            }

            if (f.exists()) {
                sharedPreferenceHelper.setBackgroundPath(f.getAbsolutePath());

                dialog.dismiss();

                Toast.makeText(requireContext(), "Background Set \n Applying Changes", Toast.LENGTH_LONG).show();

                ((MainActivity) requireContext()).findViewById(R.id.constraintLayout).setBackground(Drawable.createFromPath(f.getAbsolutePath()));

                dismiss();

            } else {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(f.getName().substring(1));

                if (!localFile.exists()) localFile.mkdirs();

                storageReference.getFile(f).addOnSuccessListener(taskSnapshot -> {

                    sharedPreferenceHelper.setBackgroundPath(f.getAbsolutePath());

                    dialog.dismiss();

                    Toast.makeText(requireContext(), "Background Set \n Applying Changes", Toast.LENGTH_LONG).show();

                    ((MainActivity) requireContext()).findViewById(R.id.constraintLayout).setBackground(Drawable.createFromPath(f.toString()));

                    dismiss();

                }).addOnFailureListener(exception -> {
                    FirebaseCrashlytics.getInstance().recordException(exception);
                    exception.printStackTrace();
                    Toast.makeText(requireContext(), "Oops! Something went wrong!", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                });
            }
        });
    }

    private void setUpRecyclerView(View inflate) {
        RecyclerView recyclerView = inflate.findViewById(R.id.imagePickRecyclerView);
        adapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                if (state.getItemCount() >= 2 && MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing())
                    MainActivity.bgDialog.dismiss();
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        final ArrayList<Uri> list = new ArrayList<>();
        final ArrayList<String> bgArrayList = new ArrayList<>();

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
        File[] files = root.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".jpg")) {
                    if (file.getName().equals(".Screenshot.jpg") || file.getName().equals(".Quotes_Background.jpg"))
                        continue;

                    list.add(Uri.fromFile(file));
                    bgArrayList.add(file.getName().substring(1));
                    if (adapter != null && getContext() != null) {
                        adapter.submitList(list);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            if (MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing())
                MainActivity.bgDialog.dismiss();
        }

        if (bgArrayList.size() == 0 && MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing()) {
            MainActivity.bgDialog.dismiss();
            Toast.makeText(requireContext(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }

        if (isNetworkAvailable(requireContext())) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images");

            storageRef.listAll().addOnSuccessListener(listResult -> {

                for (StorageReference item : listResult.getItems())
                    if (!bgArrayList.contains(item.getName()))
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            list.add(uri);
                            if (adapter != null && getContext() != null) {
                                adapter.submitList(list);
                                adapter.notifyDataSetChanged();
                            }
                        });
            });
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}