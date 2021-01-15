package phone.vishnu.quotes.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.RecyclerViewAdapter;

public class PickFragment extends Fragment {

    private RecyclerViewAdapter adapter;

    public PickFragment() {
    }

    public static PickFragment newInstance() {
        return new PickFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pick, container, false);
        setUpRecyclerView(inflate);
        if (!isNetworkAvailable(requireContext()))
            Toast.makeText(requireContext(), "Please Connect to the Internet...", Toast.LENGTH_SHORT).show();
        return inflate;
    }

    private void setUpRecyclerView(View inflate) {
        RecyclerView recyclerView = inflate.findViewById(R.id.imagePickRecyclerView);
        adapter = new RecyclerViewAdapter(requireContext());
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

        if (isNetworkAvailable(requireContext())) {

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images");

            storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    final ArrayList<Uri> list = new ArrayList<>();

                    for (StorageReference item : listResult.getItems()) {

                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                list.add(uri);
                                if (adapter != null && getContext() != null) {
                                    adapter.setArrayList(list);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(requireContext(), "Showing previously downloaded files...", Toast.LENGTH_SHORT).show();
            final ArrayList<Uri> list = new ArrayList<>();

            File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");
            File[] files = root.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.getAbsolutePath().endsWith(".jpg")) {
                        if (file.getName().equals(".Screenshot.jpg") || file.getName().equals(".Quotes_Background.jpg"))
                            continue;

                        list.add(Uri.fromFile(file));
                        if (adapter != null && getContext() != null) {
                            adapter.setArrayList(list);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
