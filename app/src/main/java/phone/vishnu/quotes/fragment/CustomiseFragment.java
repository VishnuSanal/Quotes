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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class CustomiseFragment extends Fragment implements View.OnClickListener {

    private SharedPreferenceHelper sharedPreferenceHelper;

    private ConstraintLayout constraintLayout;
    private TextView quoteTextView, authorTextView;
    private CardView cardView;
    private ImageView closeIV, moveIV, rotateIV, sizeIV;
    private boolean isMoveEnabled = false;

    public CustomiseFragment() {}

    public static CustomiseFragment newInstance() {
        return new CustomiseFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_customise, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        constraintLayout = inflate.findViewById(R.id.customiseConstraintLayout);

        quoteTextView = inflate.findViewById(R.id.customiseQuoteTextView);
        authorTextView = inflate.findViewById(R.id.customiseAuthorTextView);
        cardView = inflate.findViewById(R.id.customiseCardView);

        closeIV = inflate.findViewById(R.id.customiseCloseIV);
        moveIV = inflate.findViewById(R.id.customiseMoveIV);
        rotateIV = inflate.findViewById(R.id.customiseRotateIV);
        sizeIV = inflate.findViewById(R.id.customiseSizeIV);

        return inflate;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.e(
                "vishnu",
                "CustomiseFragment: "
                        + "\ncardView.getX() "
                        + cardView.getX()
                        + "\ncardView.getY() "
                        + cardView.getY());

        constraintLayout.post(
                () ->
                        Log.e(
                                "vishnu",
                                "CustomiseFragment: "
                                        + "\nWidth "
                                        + constraintLayout.getWidth()
                                        + "\nHeight "
                                        + constraintLayout.getHeight()));

        Quote q =
                new Quote(
                        "The first principle is that you must not fool yourself — and you are the easiest person to fool",
                        "Richard Feynman");

        String quote = q.getQuote();
        String author = q.getAuthor();

        String cardColor = sharedPreferenceHelper.getCardColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();
        float fontSize = sharedPreferenceHelper.getFontSizePreference();

        String backgroundPath = sharedPreferenceHelper.getBackgroundPath();
        if (!"-1".equals(backgroundPath))
            constraintLayout.setBackground(Drawable.createFromPath(backgroundPath));

        cardView.setCardBackgroundColor(Color.parseColor(cardColor));

        if (!(fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                quoteTextView.setTypeface(face);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        quoteTextView.setTextColor(Color.parseColor(fontColor));
        authorTextView.setTextColor(Color.parseColor(fontColor));

        quoteTextView.setTextSize(fontSize);
        authorTextView.setTextSize((float) (fontSize / 1.2));

        quoteTextView.setText(quote);
        authorTextView.setText(author);

        closeIV.setOnClickListener(this);
        moveIV.setOnClickListener(this);
        rotateIV.setOnClickListener(this);
        sizeIV.setOnClickListener(this);

        AtomicReference<Float> dX = new AtomicReference<>((float) 0);
        AtomicReference<Float> dY = new AtomicReference<>((float) 0);

        cardView.setOnTouchListener(
                (v, event) -> {
                    if (!isMoveEnabled) return false;

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX.set(v.getX() - event.getRawX());
                            dY.set(v.getY() - event.getRawY());
                            break;

                        case MotionEvent.ACTION_MOVE:
                            cardView.setX(event.getRawX() + dX.get());
                            cardView.setY(event.getRawY() + dY.get());

                            break;

                        default:
                            return false;
                    }
                    return true;
                });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.customiseCloseIV) {

            ((MainActivity) requireActivity()).updateViewPager();
            requireActivity().onBackPressed();

        } else if (id == R.id.customiseMoveIV) {

            if (!isMoveEnabled) isMoveEnabled = true;
            else {
                isMoveEnabled = false;

                int[] array = new int[2];
                cardView.getLocationOnScreen(array);

                Log.e(
                        "vishnu",
                        "CustomiseFragment#onClick: "
                                + "\nWidth "
                                + constraintLayout.getWidth() / (float) array[0]
                                + "\nHeight "
                                + constraintLayout.getHeight() / (float) array[1]);

                sharedPreferenceHelper.setCardX(constraintLayout.getWidth() / (float) array[0]);
                sharedPreferenceHelper.setCardY(constraintLayout.getHeight() / (float) array[1]);

                ((MainActivity) requireActivity()).updateViewPager();
            }

        } else if (id == R.id.customiseRotateIV) {

        } else if (id == R.id.customiseSizeIV) {

        }
    }
}
