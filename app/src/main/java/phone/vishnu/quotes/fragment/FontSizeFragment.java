/*
 * Copyright (C) 2019 - 2021 Vishnu Sanal. T
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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class FontSizeFragment extends BottomSheetDialogFragment {
    private TextView sizeReset;
    private Slider sizeSlider;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public FontSizeFragment() {}

    public static FontSizeFragment newInstance() {
        return new FontSizeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font_size, container, false);
        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());
        sizeReset = inflate.findViewById(R.id.fontSizeResetTV);
        sizeSlider = inflate.findViewById(R.id.fontSizeSlider);
        sizeSlider.setLabelFormatter(value -> (int) value + "sp");
        sizeSlider.setValue(sharedPreferenceHelper.getFontSizePreference());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sizeSlider.addOnChangeListener(
                (slider, value, fromUser) -> {
                    if (fromUser) {
                        sharedPreferenceHelper.setFontSizePreference(value);

                        ((MainActivity) requireActivity()).updateViewPager();
                    }
                });

        //      code to reset font size or make font size back to default(24sp)
        sizeReset.setOnClickListener(
                v -> {
                    sharedPreferenceHelper.setFontSizePreference(24);
                    ((MainActivity) requireActivity()).updateViewPager();
                    sizeSlider.setValue(24);
                });
    }
}
