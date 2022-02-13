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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.ColorRVAdapter;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class ColorPickFragment extends BaseBottomSheetDialogFragment {

    private ColorRVAdapter colorAdapter;

    public ColorPickFragment() {}

    public static ColorPickFragment newInstance(int COLOR_REQ_CODE) {
        return newInstance(COLOR_REQ_CODE, true);
    }

    public static ColorPickFragment newInstance(int COLOR_REQ_CODE, boolean isCancellable) {
        Bundle args = new Bundle();
        args.putInt(Constants.COLOR_REQUEST_CODE, COLOR_REQ_CODE);
        args.putBoolean(Constants.CANCELLABLE_EXTRA, isCancellable);
        ColorPickFragment fragment = new ColorPickFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            if (getArguments().containsKey(Constants.CANCELLABLE_EXTRA))
                setCancelable(getArguments().getBoolean(Constants.CANCELLABLE_EXTRA));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_color_pick, container, false);

        RecyclerView recyclerView = i.findViewById(R.id.colorPickRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        colorAdapter = new ColorRVAdapter(requireArguments().getInt(Constants.COLOR_REQUEST_CODE));

        colorAdapter.submitList(
                requireArguments().getInt(Constants.COLOR_REQUEST_CODE)
                                == Constants.PICK_FONT_COLOR_REQ_CODE
                        ? getFontColorList()
                        : getColorList());

        if (requireArguments().getInt(Constants.COLOR_REQUEST_CODE)
                == Constants.PICK_FONT_COLOR_REQ_CODE)
            ((TextView) i.findViewById(R.id.colorPickTitleTV)).setText(R.string.pick_font_color);
        else if (requireArguments().getInt(Constants.COLOR_REQUEST_CODE)
                == Constants.PICK_BG_COLOR_REQ_CODE)
            ((TextView) i.findViewById(R.id.colorPickTitleTV))
                    .setText(R.string.pick_background_color);
        else ((TextView) i.findViewById(R.id.colorPickTitleTV)).setText(R.string.pick_card_color);

        recyclerView.setAdapter(colorAdapter);

        return i;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int colorRequestCode = requireArguments().getInt(Constants.COLOR_REQUEST_CODE);

        final SharedPreferenceHelper sharedPreferenceHelper =
                new SharedPreferenceHelper(requireContext());

        colorAdapter.setOnItemClickListener(
                (colorString, isCustom) -> {
                    if (isCustom) {
                        CustomColorFragment.newInstance(colorRequestCode)
                                .show(requireActivity().getSupportFragmentManager(), getTag());

                        dismiss();
                        return;
                    }

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

    private List<String> getColorList() {
        return Arrays.asList(
                "#00000000",
                "#FFF44336",
                "#FFE91E63",
                "#FF9C27B0",
                "#FF673AB7",
                "#FF3F51B5",
                "#FF2196F3",
                "#FF03A9F4",
                "#FF00BCD4",
                "#FF009688",
                "#FF4CAF50",
                "#FF8BC34A",
                "#FFCDDC39",
                "#FFFFEB3B",
                "#FFFFC107",
                "#FFFF9800",
                "#FFFF5722",
                "#FF795548",
                "#FF9E9E9E",
                "#FF607D8B",
                "#FF000000",
                "#00000000");
    }

    private List<String> getFontColorList() {
        return Arrays.asList(
                "#00000000",
                "#FFF44336",
                "#FFE91E63",
                "#FF9C27B0",
                "#FF673AB7",
                "#FF3F51B5",
                "#FF2196F3",
                "#FF03A9F4",
                "#FF00BCD4",
                "#FF009688",
                "#FF4CAF50",
                "#FF8BC34A",
                "#FFCDDC39",
                "#FFFFEB3B",
                "#FFFFC107",
                "#FFFF9800",
                "#FFFF5722",
                "#FF795548",
                "#FF9E9E9E",
                "#FF607D8B",
                "#FF000000",
                "#FFFFFFFF");
    }
}
