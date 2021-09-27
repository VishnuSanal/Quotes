/*
 * Copyright (C) 2019 - 2019-2021 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.ArrayList;
import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.BGImageRVAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class BGImagePickFragment extends BottomSheetDialogFragment
        implements BGImageRVAdapter.OnItemClickListener {

    private BGImageRVAdapter presentAdapter, newAdapter;
    private SharedPreferenceHelper sharedPreferenceHelper;

    private CircularProgressIndicator progressBar;

    private ArrayList<String> bgNameArrayList;
    private boolean warningShown = false;

    public BGImagePickFragment() {}

    public static BGImagePickFragment newInstance() {
        return new BGImagePickFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_backgrond_image_pick, container, false);

        setUpRecyclerView(inflate);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        progressBar = inflate.findViewById(R.id.imagePickProgressBar);

        if (!isNetworkAvailable(requireContext())) {

            inflate.findViewById(R.id.imagePickNewTitleTV).setVisibility(View.GONE);

            if (!warningShown) {
                Toast.makeText(
                                requireContext(),
                                "Please connect to the Internet",
                                Toast.LENGTH_SHORT)
                        .show();
                warningShown = true;
            }
        }

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        newAdapter.setOnItemClickListener(this);

        presentAdapter.setOnItemClickListener(this);
    }

    private void setUpRecyclerView(View inflate) {
        bgNameArrayList = new ArrayList<>();
        loadPresentImages(inflate);
        loadNewImages(inflate);
    }

    private void loadPresentImages(View inflate) {

        RecyclerView presentRecyclerView = inflate.findViewById(R.id.imagePickPresentRecyclerView);
        presentAdapter = new BGImageRVAdapter();
        presentRecyclerView.setAdapter(presentAdapter);

        presentRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                    @Override
                    public void onLayoutCompleted(RecyclerView.State state) {
                        super.onLayoutCompleted(state);
                        if (state.getItemCount() > 0
                                && MainActivity.bgDialog != null
                                && MainActivity.bgDialog.isShowing())
                            MainActivity.bgDialog.dismiss();

                        if (state.getItemCount() > 4) progressBar.setVisibility(View.GONE);
                    }
                };
        presentRecyclerView.setLayoutManager(layoutManager);

        final ArrayList<Uri> list = new ArrayList<>();
        bgNameArrayList = new ArrayList<>();

        String[] files = requireContext().fileList();

        if (files != null) {
            for (String s : files) {
                File file = new File(s);

                if (file.getAbsolutePath().endsWith(".jpg")) {
                    if (file.getName().equals("background.jpg")
                            || file.getName().equals("screenshot.jpg")) continue;

                    list.add(Uri.fromFile(file));
                    bgNameArrayList.add(file.getName());

                    if (presentAdapter != null && getContext() != null) {
                        presentAdapter.submitList(list);
                        presentAdapter.notifyDataSetChanged();
                    }
                }
            }
        } else {
            if (MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing())
                MainActivity.bgDialog.dismiss();
        }

        if (bgNameArrayList.size() == 0
                && MainActivity.bgDialog != null
                && MainActivity.bgDialog.isShowing()) {
            MainActivity.bgDialog.dismiss();
        }
    }

    private void loadNewImages(View inflate) {
        RecyclerView newRecyclerView = inflate.findViewById(R.id.imagePickNewRecyclerView);
        newAdapter = new BGImageRVAdapter();
        newRecyclerView.setAdapter(newAdapter);

        newRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                    @Override
                    public void onLayoutCompleted(RecyclerView.State state) {
                        super.onLayoutCompleted(state);
                        if (state.getItemCount() > 0
                                && MainActivity.bgDialog != null
                                && MainActivity.bgDialog.isShowing())
                            MainActivity.bgDialog.dismiss();

                        if (state.getItemCount() > 4) progressBar.setVisibility(View.GONE);
                    }
                };
        newRecyclerView.setLayoutManager(layoutManager);

        if (bgNameArrayList.isEmpty()) {
            inflate.findViewById(R.id.imagePickPresentTitleTV).setVisibility(View.GONE);
        }

        final ArrayList<Uri> list = new ArrayList<>();

        if (isNetworkAvailable(requireContext())) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("images");

            storageRef
                    .listAll()
                    .addOnSuccessListener(
                            listResult -> {
                                for (StorageReference item : listResult.getItems())
                                    if (!bgNameArrayList.contains(item.getName()))
                                        item.getDownloadUrl()
                                                .addOnSuccessListener(
                                                        uri -> {
                                                            list.add(uri);
                                                            if (newAdapter != null
                                                                    && getContext() != null) {
                                                                newAdapter.submitList(list);
                                                                newAdapter.notifyDataSetChanged();
                                                            }
                                                        });
                            });
        }
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onItemClick(Uri uri) {
        final ProgressDialog dialog = ProgressDialog.show(requireContext(), "", "Please Wait....");

        final File f;

        if (uri.toString()
                .contains(
                        (BuildConfig.DEBUG)
                                ? "https://firebasestorage.googleapis.com/v0/b/quotes-debug-q.appspot.com"
                                : "https://firebasestorage.googleapis.com/v0/b/quotes-q.appspot.com")) {

            String fileName = String.valueOf(uri).split("%2F")[1].split("\\?")[0];

            f = new File(requireContext().getFilesDir(), fileName);

        } else {
            f =
                    new File(
                            uri.toString()
                                    .replace("file://", requireContext().getFilesDir().getPath()));
        }

        if (f.exists()) {
            sharedPreferenceHelper.setBackgroundPath(f.getAbsolutePath());

            dialog.dismiss();

            Toast.makeText(
                            requireContext(),
                            "Background Set \n Applying Changes",
                            Toast.LENGTH_LONG)
                    .show();

            ((MainActivity) requireContext())
                    .findViewById(R.id.constraintLayout)
                    .setBackground(Drawable.createFromPath(f.getAbsolutePath()));

            dismiss();

        } else {
            StorageReference storageReference =
                    FirebaseStorage.getInstance().getReference().child("images").child(f.getName());

            storageReference
                    .getFile(f)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                sharedPreferenceHelper.setBackgroundPath(f.getAbsolutePath());

                                dialog.dismiss();

                                Toast.makeText(
                                                requireContext(),
                                                "Background Set \n Applying Changes",
                                                Toast.LENGTH_LONG)
                                        .show();

                                ((MainActivity) requireContext())
                                        .findViewById(R.id.constraintLayout)
                                        .setBackground(Drawable.createFromPath(f.toString()));

                                dismiss();
                            })
                    .addOnFailureListener(
                            exception -> {
                                exception.printStackTrace();
                                Toast.makeText(
                                                requireContext(),
                                                "Oops! Something went wrong!",
                                                Toast.LENGTH_LONG)
                                        .show();
                                dialog.dismiss();
                            });
        }
    }
}
