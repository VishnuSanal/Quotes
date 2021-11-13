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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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

    private boolean warningShown = false;

    public FontFragment() {}

    public static FontFragment newInstance() {
        return new FontFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font, container, false);

        if (!warningShown && !isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), "Please connect to the Internet", Toast.LENGTH_SHORT)
                    .show();
            warningShown = true;
        }

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

        final String[] files = requireContext().fileList();

        if (files != null) {

            for (String s : files) {

                File file = new File(s);

                if (file.getAbsolutePath().endsWith(".ttf")) {

                    String fontString = file.getName().replace(".ttf", "");

                    fontString = fontString.toUpperCase().charAt(0) + fontString.substring(1);

                    fontList.add(fontString);
                }
            }
            if (getContext() != null)
                if (fontDataAdapter == null) {
                    fontDataAdapter =
                            new FontLVAdapter(Objects.requireNonNull(requireContext()), fontList);
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
            storageRef
                    .listAll()
                    .addOnSuccessListener(
                            listResult -> {
                                for (StorageReference item : listResult.getItems()) {

                                    String fontString = item.getName().replace(".ttf", "");

                                    fontString =
                                            fontString.toUpperCase().charAt(0)
                                                    + fontString.substring(1);

                                    ArrayList<String> toBeRemoved =
                                            sharedPreferenceHelper.getFontListToBeRemoved();

                                    if (!fontList.contains(fontString)
                                            && !toBeRemoved.contains(
                                                    item.getName().toLowerCase())) {

                                        if (getContext() != null && fontDataAdapter != null) {
                                            fontDataAdapter.add(fontString);
                                            fontDataAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }

                                progressBar.setVisibility(View.GONE);
                            });
        }

        listView.setOnItemClickListener(
                (parent, view1, position, id) -> {
                    final ProgressDialog progressDialog =
                            ProgressDialog.show(requireContext(), "", "Please Wait....");

                    String fontString = fontList.get(position).toLowerCase() + ".ttf";

                    StorageReference storageReference =
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child("fonts")
                                    .child(fontString);

                    final File f = new File(requireContext().getFilesDir(), fontString);

                    if (f.exists()) {
                        sharedPreferenceHelper.setFontPath(f.toString());

                        Toast.makeText(
                                        requireContext(),
                                        "Font Set \n Applying Changes",
                                        Toast.LENGTH_SHORT)
                                .show();
                        progressDialog.dismiss();

                        ((MainActivity) requireContext())
                                .getQuoteViewPagerAdapter()
                                .notifyDataSetChanged();

                        dismiss();

                    } else {

                        storageReference
                                .getFile(f)
                                .addOnSuccessListener(
                                        taskSnapshot -> {
                                            if (getActivity() != null) {

                                                sharedPreferenceHelper.setFontPath(f.toString());

                                                Toast.makeText(
                                                                requireContext(),
                                                                "Font Set \n Applying Changes",
                                                                Toast.LENGTH_SHORT)
                                                        .show();
                                                progressDialog.dismiss();

                                                ((MainActivity) requireContext())
                                                        .getQuoteViewPagerAdapter()
                                                        .notifyDataSetChanged();

                                                dismiss();

                                            } else {
                                                progressDialog.dismiss();
                                            }
                                        })
                                .addOnFailureListener(
                                        exception -> {
                                            exception.printStackTrace();
                                            progressDialog.dismiss();
                                            Toast.makeText(
                                                            requireContext(),
                                                            "Oops! Something went wrong!",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        });
                    }
                });
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
