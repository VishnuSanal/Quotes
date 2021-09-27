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

import static phone.vishnu.quotes.activity.MainActivity.bgDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;

public class BGOptionPickFragment extends BottomSheetDialogFragment {

    private static final String CANCELLABLE_EXTRA = "isCancellable";

    private RadioGroup radioGroup;

    public static BGOptionPickFragment newInstance(boolean isCancellable) {
        BGOptionPickFragment bottomSheetFragment = new BGOptionPickFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(CANCELLABLE_EXTRA, isCancellable);

        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        if (getArguments() != null)
            if (getArguments().containsKey(CANCELLABLE_EXTRA))
                setCancelable(getArguments().getBoolean(CANCELLABLE_EXTRA));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.background_option_pick_dialog, container, false);

        radioGroup = inflate.findViewById(R.id.backgroundPickRadioGroup);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener(
                (group, id) -> {
                    if (id == R.id.backgroundPickColorRadioButton) {
                        ColorPickFragment.newInstance(ColorPickFragment.PICK_BG_COLOR_REQ_CODE)
                                .show(
                                        requireActivity().getSupportFragmentManager(),
                                        "ColorPickFragment");

                        dismiss();

                    } else if (id == R.id.backgroundPickGalleryRadioButton) {

                        requireActivity()
                                .startActivityForResult(
                                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                                        MainActivity.PICK_IMAGE_ID);

                        dismiss();

                    } else if (id == R.id.backgroundPickDefaultRadioButton) {

                        bgDialog = new ProgressDialog(requireContext(), R.style.DialogTheme);
                        bgDialog.setMessage("Please Wait....");
                        bgDialog.show();
                        bgDialog.setCancelable(false);

                        BGImagePickFragment.newInstance()
                                .show(
                                        requireActivity().getSupportFragmentManager(),
                                        "BGImagePickFragment");

                        dismiss();
                    }
                });
    }
}
