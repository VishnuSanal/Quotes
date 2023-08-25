/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
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

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import phone.vishnu.quotes.request.InputStreamVolleyRequest;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.BGImageRVAdapter;
import phone.vishnu.quotes.adapter.UnsplashRVAdapter;
import phone.vishnu.quotes.controller.AppController;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.UnsplashItem;
import phone.vishnu.quotes.viewmodel.BGImagePickViewModel;

public class BGImagePickFragment extends BaseBottomSheetDialogFragment
        implements BGImageRVAdapter.OnItemClickListener, UnsplashRVAdapter.OnItemClickListener {

    private SharedPreferenceHelper sharedPreferenceHelper;

    private LinearProgressIndicator progressBar, newProgressBar;

    private TextInputEditText textInputEditText;
    private TextInputLayout textInputLayout;

    private TextView presentTitleTV, newTitleTV, unsplashCreditTV;

    private BGImagePickViewModel viewModel;

    private BGImageRVAdapter presentAdapter;
    private UnsplashRVAdapter newAdapter;
    private RecyclerView presentRecyclerView, newRecyclerView;

    private boolean warningShown = false;

    private String quotesstatuscreator;

    public BGImagePickFragment() {}

    public static BGImagePickFragment newInstance() {
        return new BGImagePickFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_backgrond_image_pick, container, false);

        quotesstatuscreator =
                new StringBuilder(
                                Arrays.toString(
                                                getResources()
                                                        .getTextArray(R.array.quotesstatuscreator))
                                        .replace("[", "")
                                        .replace("]", "")
                                        .replace(",", "")
                                        .replace(" ", "")
                                        .trim())
                        .reverse()
                        .toString();

        viewModel =
                new ViewModelProvider(
                                this,
                                new ViewModelProvider.AndroidViewModelFactory(
                                        (Application) requireContext().getApplicationContext()))
                        .get(BGImagePickViewModel.class);

        setUpRecyclerView(inflate);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        textInputEditText = inflate.findViewById(R.id.imagePickSearchTIE);
        textInputLayout = inflate.findViewById(R.id.imagePickSearchTIL);
        presentTitleTV = inflate.findViewById(R.id.imagePickPresentTitleTV);
        newTitleTV = inflate.findViewById(R.id.imagePickNewTitleTV);
        unsplashCreditTV = inflate.findViewById(R.id.imagePickUnsplashCreditTV);

        progressBar = inflate.findViewById(R.id.imagePickProgressBar);
        newProgressBar = inflate.findViewById(R.id.imagePickNewProgressBar);

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

        textInputLayout.setEndIconOnClickListener(this::onClick);

        unsplashCreditTV.setOnClickListener(
                v ->
                        requireContext()
                                .startActivity(
                                        new Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(
                                                        "https://unsplash.com/?utm_source=Quotes%20Status%20Creator%26utm_medium=referral"))));

        textInputEditText.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                            || actionId == EditorInfo.IME_ACTION_DONE) this.onClick(v);
                    return false;
                });
    }

    private void setUpRecyclerView(View inflate) {
        presentRecyclerView = inflate.findViewById(R.id.imagePickPresentRecyclerView);
        presentAdapter = new BGImageRVAdapter();
        presentRecyclerView.setAdapter(presentAdapter);
        presentRecyclerView.setHasFixedSize(false);
        presentRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                    @Override
                    public void onLayoutCompleted(RecyclerView.State state) {
                        super.onLayoutCompleted(state);

                        dismissProgressDialog();

                        if (state.getItemCount() > 4) progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        newRecyclerView = inflate.findViewById(R.id.imagePickNewRecyclerView);
        newAdapter = new UnsplashRVAdapter();
        newRecyclerView.setAdapter(newAdapter);
        presentRecyclerView.setHasFixedSize(false);
        newRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                    @Override
                    public void onLayoutCompleted(RecyclerView.State state) {
                        super.onLayoutCompleted(state);

                        dismissProgressDialog();

                        if (state.getItemCount() > 4) progressBar.setVisibility(View.INVISIBLE);
                    }
                });

        loadPresentImages();
        loadNewImages();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPresentImages() {
        MutableLiveData<List<Uri>> presentImagesMutableLiveData = viewModel.getPresentImages();

        if (presentImagesMutableLiveData != null)
            presentImagesMutableLiveData.observe(
                    this,
                    list -> {
                        presentAdapter.submitList(new ArrayList<>(list));
                        presentAdapter.notifyDataSetChanged();
                        presentRecyclerView.invalidate();
                    });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadNewImages() {
        MutableLiveData<List<UnsplashItem>> searchImagesMutableLiveData =
                viewModel.searchImages("wallpaper", quotesstatuscreator);

        if (searchImagesMutableLiveData != null)
            searchImagesMutableLiveData.observe(
                    this,
                    list -> {
                        newAdapter.submitList(new ArrayList<>(list));
                        newAdapter.notifyDataSetChanged();
                        newRecyclerView.invalidate();
                    });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onItemClick(Uri uri) {

        String s = uri.toString().replace("file://", requireContext().getFilesDir().getPath());

        ((MainActivity) requireActivity())
                .findViewById(R.id.constraintLayout)
                .setBackground(Drawable.createFromPath(s));

        new SharedPreferenceHelper(requireContext()).setBackgroundPath(s);

        dismiss();
    }

    @Override
    public void onItemClick(UnsplashItem item) {

        if (!isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No network connection", Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }

        if (MainActivity.bgDialog != null) MainActivity.bgDialog.show();

        String targetPath =
                requireContext().getFilesDir().getPath()
                        + File.separator
                        + item.getRegularUri().getLastPathSegment()
                        + ".jpg";

        downloadImage(
                String.valueOf(item.getRegularUri()),
                targetPath,
                () -> {
                    sharedPreferenceHelper.setBackgroundPath(targetPath);

                    ((MainActivity) requireActivity())
                            .findViewById(R.id.constraintLayout)
                            .setBackground(Drawable.createFromPath(targetPath));

                    AppController.getInstance()
                            .addToRequestQueue(
                                    new StringRequest(
                                            Request.Method.GET,
                                            item.getDownloadTrigger().toString()
                                                    + "?client_id="
                                                    + quotesstatuscreator,
                                            null,
                                            null));

                    dismissProgressDialog();
                    dismiss();
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void onClick(View v) {

        newProgressBar.setVisibility(View.VISIBLE);

        presentRecyclerView.setVisibility(View.GONE);
        presentTitleTV.setVisibility(View.GONE);

        if (textInputEditText.getText() != null) {

            MutableLiveData<List<UnsplashItem>> searchImagesMutableLiveData =
                    viewModel.searchImages(
                            textInputEditText.getText().toString(), quotesstatuscreator);

            if (searchImagesMutableLiveData != null)
                searchImagesMutableLiveData.observe(
                        this,
                        list -> {
                            newAdapter.submitList(new ArrayList<>(list));
                            newAdapter.notifyDataSetChanged();
                            newRecyclerView.invalidate();

                            new Handler()
                                    .postDelayed(
                                            () -> {
                                                if (newProgressBar != null
                                                        && newProgressBar.getVisibility()
                                                                == View.VISIBLE)
                                                    newProgressBar.setVisibility(View.GONE);
                                            },
                                            200);
                        });
        }
    }

    private void dismissProgressDialog() {
        if (MainActivity.bgDialog != null && MainActivity.bgDialog.isShowing())
            MainActivity.bgDialog.dismiss();
    }

    private void downloadImage(String url, String targetPath, Runnable onFinish) {
        AppController.getInstance()
                .addToRequestQueue(
                        new InputStreamVolleyRequest(
                                Request.Method.GET,
                                url,
                                (Response.Listener<byte[]>)
                                        response -> {
                                            try {

                                                File file = new File(targetPath);

                                                FileOutputStream fileOutputStream =
                                                        new FileOutputStream(file);

                                                fileOutputStream.write(response);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();

                                                onFinish.run();

                                            } catch (IOException | NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                        },
                                Throwable::printStackTrace,
                                null));
    }
}
