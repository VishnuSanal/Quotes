package phone.vishnu.quotes.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.RecyclerViewAdapter;

public class PickFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    public PickFragment() {
    }

    public static PickFragment newInstance() {
        return new PickFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pick, container, false);

        recyclerView = inflate.findViewById(R.id.my_recycler_view);

        adapter = new RecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        setUpRecyclerView();

        return inflate;
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);

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
                            if (adapter != null) {
                                adapter = new RecyclerViewAdapter(getActivity(), list);
                                recyclerView.setAdapter(adapter);

                                RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                                    @Override
                                    public void onLayoutCompleted(RecyclerView.State state) {
                                        super.onLayoutCompleted(state);
                                        if (state.getItemCount() >= 1 && MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing())
                                            MainActivity.bgDialog.dismiss();

                                    }
                                };
                                recyclerView.setLayoutManager(layoutManager);

                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

            }
        });
    }
}
