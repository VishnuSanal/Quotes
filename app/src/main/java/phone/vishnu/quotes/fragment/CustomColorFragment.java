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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.skydoves.colorpickerview.ColorPickerView;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.helper.CustomFlag;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class CustomColorFragment extends BaseBottomSheetDialogFragment {

    private Button submitButton, cancelButton;
    private ColorPickerView colorPickerView;

    private SharedPreferenceHelper sharedPreferenceHelper;

    public CustomColorFragment() {}

    public static CustomColorFragment newInstance(int COLOR_PICK_REQ_CODE) {
        CustomColorFragment fragment = new CustomColorFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.COLOR_PICK_REQUEST_CODE, COLOR_PICK_REQ_CODE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_custom_color, container, false);

        submitButton = inflate.findViewById(R.id.customColorSubmitButton);
        cancelButton = inflate.findViewById(R.id.customColorCancelButton);
        colorPickerView = inflate.findViewById(R.id.customColorColorPicker);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        //        colorPickerView.setInitialColor(); TODO:

        colorPickerView.setFlagView(new CustomFlag(requireContext(), R.layout.custom_color_flag));

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancelButton.setOnClickListener(v -> dismiss());

        submitButton.setOnClickListener(
                v -> {
                    final int colorRequestCode =
                            requireArguments().getInt(Constants.COLOR_PICK_REQUEST_CODE);

                    String colorString =
                            "#" + colorPickerView.getColorEnvelope().getHexCode().substring(2);

                    if (colorRequestCode == Constants.PICK_BG_COLOR_REQ_CODE) {

                        DisplayMetrics metrics = new DisplayMetrics();
                        metrics.widthPixels = 1080;
                        metrics.heightPixels = 1920;

                        Bitmap image =
                                Bitmap.createBitmap(
                                        metrics.widthPixels,
                                        metrics.heightPixels,
                                        Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(image);
                        canvas.drawColor(Color.parseColor(colorString));

                        new ExportHelper(requireContext()).exportBackgroundImage(image);

                        ((MainActivity) requireActivity())
                                .setConstraintLayoutBackground(
                                        Drawable.createFromPath(
                                                sharedPreferenceHelper.getBackgroundPath()));

                        dismiss();

                    } else if (colorRequestCode == Constants.PICK_CARD_COLOR_REQ_CODE) {

                        sharedPreferenceHelper.setColorPreference(colorString);

                        ((MainActivity) requireActivity()).updateViewPager();

                        dismiss();

                    } else if (colorRequestCode == Constants.PICK_FONT_COLOR_REQ_CODE) {

                        if (colorString.equals("#00000000")) colorString = "#FFFFFF";

                        sharedPreferenceHelper.setFontColorPreference(colorString);

                        ((MainActivity) requireActivity()).updateViewPager();

                        dismiss();
                    }
                });
    }
}
