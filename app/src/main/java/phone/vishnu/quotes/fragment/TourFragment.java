/*
 * Copyright (C) 2019 - 2024 Vishnu Sanal. T
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.adapter.TourViewPagerAdapter;
import phone.vishnu.quotes.controller.AppController;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.repository.FontsRepository;
import phone.vishnu.quotes.request.InputStreamVolleyRequest;

public class TourFragment extends Fragment {

    private ViewPager2 viewPager;

    private TourViewPagerAdapter adapter;
    private Button nextButton, skipButton;

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Nullable private ProgressDialog dialog;

    public TourFragment() {}

    public static TourFragment newInstance() {
        return new TourFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_tour, container, false);

        viewPager = inflate.findViewById(R.id.splashScreenTourViewPager);
        TabLayout tabLayout = inflate.findViewById(R.id.splashScreenTourTabLayout);

        nextButton = inflate.findViewById(R.id.splashScreenNextButton);
        skipButton = inflate.findViewById(R.id.splashScreenSkipButton);

        adapter = new TourViewPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {}).attach();

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPermission();

        final int pageCount = adapter.getItemCount() - 1;

        nextButton.setOnClickListener(
                v -> {
                    if (viewPager.getCurrentItem() < pageCount)
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    else {
                        checkDownloadStatusAndMoveToNext();
                    }
                });

        skipButton.setOnClickListener(v -> checkDownloadStatusAndMoveToNext());

        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {

                    private boolean settled = false;

                    private int position = 0;

                    @Override
                    public void onPageScrolled(
                            int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        this.position = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        super.onPageScrollStateChanged(state);

                        if (state == ViewPager2.SCROLL_STATE_DRAGGING) settled = false;

                        if (state == ViewPager2.SCROLL_STATE_SETTLING) settled = true;

                        if (state == ViewPager2.SCROLL_STATE_IDLE
                                && !settled
                                && position == adapter.getItemCount() - 1)
                            checkDownloadStatusAndMoveToNext();
                    }
                });
    }

    private void requestPermission() {
        Dexter.withContext(requireContext())
                .withPermissions(
                        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(
                        new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(
                                    MultiplePermissionsReport multiplePermissionsReport) {
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    initDownloadTasks();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(
                                    List<PermissionRequest> list,
                                    PermissionToken permissionToken) {}
                        });

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET)
                        == PackageManager.PERMISSION_GRANTED
                && isNetworkAvailable()) {
            initDownloadTasks();
        }
    }

    private void checkDownloadStatusAndMoveToNext() {
        sharedPreferenceHelper.setNewFirstRunBoolean(false);
        sharedPreferenceHelper.setFirstRunBoolean(false);

        File fontFile = new File(requireContext().getFilesDir(), "forum.ttf");
        File imageFile = new File(requireContext().getFilesDir(), "background.jpg");

        if (fontFile.exists() && imageFile.exists()) {
            moveToNext();
            return;
        }

        try {
            dialog = new ProgressDialog(requireContext(), R.style.DialogTheme);
            dialog.setMessage(getString(R.string.please_wait));
            dialog.show();
            dialog.setCancelable(false);
            new Handler()
                    .postDelayed(
                            () -> {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.cancel();
                                    moveToNext();
                                }
                            },
                            2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDownloadTasks() {
        File fontFile = new File(requireContext().getFilesDir(), "forum.ttf");
        File imageFile = new File(requireContext().getFilesDir(), "background.jpg");

        downloadImage(fontFile, imageFile);
    }

    private void moveToNext() {
        ((SplashActivity) requireActivity()).moveToNext();
    }

    private void downloadImage(File fontFile, File imageFile) {

        if (imageFile.exists()) {
            sharedPreferenceHelper.setColorPreference("#00000000");
            sharedPreferenceHelper.setBackgroundPath(imageFile.toString());

            downloadFont(fontFile);
            return;
        }

        String url =
                "https://images.unsplash.com/photo-1517167685284-96a27681ad75?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wyODEzNTV8MHwxfHNlYXJjaHwyMHx8d2FsbHBhcGVyfGVufDF8MXx8fDE2OTI5NzY2NTN8MA&ixlib=rb-4.0.3&q=80&w=1080";

        String quotesstatuscreator =
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

        AppController.getInstance()
                .addToRequestQueue(
                        new InputStreamVolleyRequest(
                                Request.Method.GET,
                                url,
                                (Response.Listener<byte[]>)
                                        response -> {
                                            try {

                                                File file = new File(imageFile.toString());

                                                FileOutputStream fileOutputStream =
                                                        new FileOutputStream(file);

                                                fileOutputStream.write(response);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();

                                                sharedPreferenceHelper.setColorPreference(
                                                        "#00000000");
                                                sharedPreferenceHelper.setBackgroundPath(
                                                        imageFile.toString());

                                                AppController.getInstance()
                                                        .addToRequestQueue(
                                                                new StringRequest(
                                                                        Request.Method.GET,
                                                                        "https://api.unsplash.com/photos/WqPAETBU2G8/download?ixid=M3wyODEzNTV8MHwxfHNlYXJjaHwyMHx8d2FsbHBhcGVyfGVufDF8MXx8fDE2OTI5NzY2NTN8MA"
                                                                                + "?client_id="
                                                                                + quotesstatuscreator,
                                                                        null,
                                                                        null));
                                            } catch (IOException | NullPointerException e) {
                                                e.printStackTrace();
                                            }
                                            downloadFont(fontFile);
                                        },
                                Throwable::printStackTrace,
                                null));
    }

    private void downloadFont(File f) {

        if (f.exists()) {
            sharedPreferenceHelper.setFontPath(f.toString());
            if (dialog != null && dialog.isShowing()) dialog.cancel();
            return;
        }

        String fontURL = FontsRepository.fontPrefix + "forum.ttf" + "?raw=true";

        AppController.getInstance()
                .addToRequestQueue(
                        new InputStreamVolleyRequest(
                                Request.Method.GET,
                                fontURL,
                                (Response.Listener<byte[]>)
                                        response -> {
                                            try {

                                                File file = new File(f.toString());

                                                FileOutputStream fileOutputStream =
                                                        new FileOutputStream(file);

                                                fileOutputStream.write(response);
                                                fileOutputStream.flush();
                                                fileOutputStream.close();

                                                sharedPreferenceHelper.setFontPath(f.toString());

                                            } catch (IOException | NullPointerException e) {
                                                e.printStackTrace();
                                            }

                                            if (dialog != null && dialog.isShowing())
                                                dialog.cancel();
                                        },
                                Throwable::printStackTrace,
                                null));
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager)
                        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission")
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
