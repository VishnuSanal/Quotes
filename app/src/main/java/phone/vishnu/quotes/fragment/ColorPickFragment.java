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

import android.annotation.SuppressLint;
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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.adapter.ColorRVAdapter;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class ColorPickFragment extends BottomSheetDialogFragment {

    public static final int PICK_BG_COLOR_REQ_CODE = 0;
    public static final int PICK_CARD_COLOR_REQ_CODE = 1;
    public static final int PICK_FONT_COLOR_REQ_CODE = 2;

    private ColorRVAdapter colorAdapter;

    public ColorPickFragment() {}

    public static ColorPickFragment newInstance(int COLOR_REQ_CODE) {
        Bundle args = new Bundle();
        args.putInt("ColorRequestCode", COLOR_REQ_CODE);
        ColorPickFragment fragment = new ColorPickFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.fragment_color_pick, container, false);

        RecyclerView recyclerView = i.findViewById(R.id.colorPickRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        colorAdapter =
                new ColorRVAdapter(
                        Objects.requireNonNull(getArguments()).getInt("ColorRequestCode"));

        colorAdapter.submitList(
                Objects.requireNonNull(getArguments()).getInt("ColorRequestCode")
                                == PICK_FONT_COLOR_REQ_CODE
                        ? getFontColorList()
                        : getColorList());

        if (Objects.requireNonNull(getArguments()).getInt("ColorRequestCode")
                == PICK_FONT_COLOR_REQ_CODE)
            ((TextView) i.findViewById(R.id.colorPickTitleTV)).setText("Pick Font Color");
        else if (Objects.requireNonNull(getArguments()).getInt("ColorRequestCode")
                == PICK_BG_COLOR_REQ_CODE)
            ((TextView) i.findViewById(R.id.colorPickTitleTV)).setText("Pick Background Color");
        else ((TextView) i.findViewById(R.id.colorPickTitleTV)).setText("Pick Card Color");

        recyclerView.setAdapter(colorAdapter);

        return i;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int colorRequestCode =
                Objects.requireNonNull(getArguments()).getInt("ColorRequestCode");

        final SharedPreferenceHelper sharedPreferenceHelper =
                new SharedPreferenceHelper(requireContext());

        colorAdapter.setOnItemClickListener(
                colorString -> {
                    if (colorRequestCode == PICK_BG_COLOR_REQ_CODE) {

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

                    } else if (colorRequestCode == PICK_CARD_COLOR_REQ_CODE) {

                        sharedPreferenceHelper.setColorPreference(colorString);

                        ((MainActivity) requireActivity()).updateViewPager();

                        dismiss();

                    } else if (colorRequestCode == PICK_FONT_COLOR_REQ_CODE) {

                        if (colorString.equals("#00000000")) colorString = "#FFFFFF";

                        sharedPreferenceHelper.setFontColorPreference(colorString);

                        ((MainActivity) requireActivity()).updateViewPager();

                        dismiss();
                    }
                });
    }

    private List<String> getColorList() {
        return Arrays.asList(
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
