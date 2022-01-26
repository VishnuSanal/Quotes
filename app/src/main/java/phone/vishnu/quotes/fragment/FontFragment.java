/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.FontLVAdapter;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.helper.FileUtils;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class FontFragment extends BaseBottomSheetDialogFragment {

    private static final int FONT_PICK_REQUEST_CODE = 2;

    private final ArrayList<String> fontList = new ArrayList<>();
    private SharedPreferenceHelper sharedPreferenceHelper;
    private ListView listView;
    private FontLVAdapter fontDataAdapter;
    private LinearProgressIndicator progressBar;
    private TextView openTV;

    private boolean warningShown = false;

    public FontFragment() {}

    public static FontFragment newInstance() {
        return new FontFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font, container, false);

        if (!warningShown && !isNetworkAvailable(requireContext())) {
            Toast.makeText(
                            requireContext(),
                            getString(R.string.please_connect_to_the_internet),
                            Toast.LENGTH_SHORT)
                    .show();
            warningShown = true;
        }

        progressBar = inflate.findViewById(R.id.fontProgressBar);
        listView = inflate.findViewById(R.id.fontListView);
        openTV = inflate.findViewById(R.id.fontOpenTV);

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
            StorageReference storageRef = storage.getReference().child(Constants.FONTS);
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
                            ProgressDialog.show(
                                    requireContext(), "", getString(R.string.please_wait));

                    String fontString = fontList.get(position).toLowerCase() + ".ttf";

                    StorageReference storageReference =
                            FirebaseStorage.getInstance()
                                    .getReference()
                                    .child(Constants.FONTS)
                                    .child(fontString);

                    final File f = new File(requireContext().getFilesDir(), fontString);

                    if (f.exists()) {
                        sharedPreferenceHelper.setFontPath(f.toString());

                        Toast.makeText(
                                        requireContext(),
                                        String.format(
                                                "%s\n%s",
                                                getString(R.string.font_set),
                                                getString(R.string.applying_changes)),
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
                                                                String.format(
                                                                        "%s\n%s",
                                                                        getString(
                                                                                R.string.font_set),
                                                                        getString(
                                                                                R.string
                                                                                        .applying_changes)),
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
                                                            getString(
                                                                    R.string
                                                                            .oops_something_went_wrong),
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                        });
                    }
                });

        openTV.setOnClickListener(
                v ->
                        startActivityForResult(
                                new Intent(Intent.ACTION_OPEN_DOCUMENT)
                                        .addCategory(Intent.CATEGORY_OPENABLE)
                                        .setType("*/*")
                                        .putExtra(
                                                Intent.EXTRA_MIME_TYPES, new String[] {"font/ttf"}),
                                FONT_PICK_REQUEST_CODE));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK
                && requestCode == FontFragment.FONT_PICK_REQUEST_CODE
                && data != null) {

            final ProgressDialog progressDialog =
                    ProgressDialog.show(requireContext(), "", getString(R.string.please_wait));

            String path = FileUtils.copy(requireContext(), data.getData());

            if (path == null) {
                Toast.makeText(
                                requireContext(),
                                getString(R.string.oops_something_went_wrong),
                                Toast.LENGTH_LONG)
                        .show();
                progressDialog.dismiss();
                dismiss();
                return;
            }

            sharedPreferenceHelper.setFontPath(path);

            Toast.makeText(
                            requireContext(),
                            String.format(
                                    "%s\n%s",
                                    getString(R.string.font_set),
                                    getString(R.string.applying_changes)),
                            Toast.LENGTH_SHORT)
                    .show();
            progressDialog.dismiss();

            ((MainActivity) requireContext()).getQuoteViewPagerAdapter().notifyDataSetChanged();

            dismiss();

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
