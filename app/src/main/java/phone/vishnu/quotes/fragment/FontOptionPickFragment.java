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

import static phone.vishnu.quotes.activity.MainActivity.fontDialog;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.Constants;

public class FontOptionPickFragment extends BaseBottomSheetDialogFragment {

    private RadioGroup radioGroup;

    public FontOptionPickFragment() {}

    public static FontOptionPickFragment newInstance() {
        return new FontOptionPickFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.font_option_pick_dialog, container, false);

        radioGroup = inflate.findViewById(R.id.fontOptionPickRadioGroup);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener(
                (group, id) -> {
                    if (id == R.id.fontOptionPickFontRadioButton) {

                        fontDialog = new ProgressDialog(requireContext(), R.style.DialogTheme);
                        fontDialog.setMessage(getString(R.string.please_wait));
                        fontDialog.show();
                        fontDialog.setCancelable(false);

                        FontFragment.newInstance()
                                .show(
                                        requireActivity().getSupportFragmentManager(),
                                        "FontOptionPickFragment");

                        dismiss();

                    } else if (id == R.id.fontOptionPickColorRadioButton) {

                        ColorPickFragment.newInstance(Constants.PICK_FONT_COLOR_REQ_CODE)
                                .show(
                                        requireActivity().getSupportFragmentManager(),
                                        "FontOptionPickFragment");

                        dismiss();

                    } else if (id == R.id.fontOptionPickSizeRadioButton) {

                        FontSizeFragment.newInstance()
                                .show(
                                        requireActivity().getSupportFragmentManager(),
                                        "FontOptionPickFragment");

                        dismiss();
                    }
                });
    }
}
