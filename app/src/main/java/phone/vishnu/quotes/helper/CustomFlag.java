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

package phone.vishnu.quotes.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.TextView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.flag.FlagView;
import phone.vishnu.quotes.R;

@SuppressLint("ViewConstructor")
public class CustomFlag extends FlagView {

    private final TextView textView;
    private final ImageView imageView;

    public CustomFlag(Context context, int layout) {
        super(context, layout);
        textView = findViewById(R.id.customColorFlagTV);
        imageView = findViewById(R.id.customColorFlagIV);
    }

    @Override
    public void onRefresh(ColorEnvelope colorEnvelope) {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(colorEnvelope.getColor());
        gradientDrawable.setCornerRadius(Utils.Companion.DPtoPX(getContext(), 96));

        imageView.setBackgroundDrawable(gradientDrawable);

        textView.setText(String.format("#%s", colorEnvelope.getHexCode().substring(2)));
    }
}
