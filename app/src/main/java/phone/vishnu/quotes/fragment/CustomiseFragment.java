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

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class CustomiseFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;

    private ConstraintLayout constraintLayout;
    private TextView quoteTextView, authorTextView;
    private CardView cardView;
    private ImageView moveIV;

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

        moveIV = inflate.findViewById(R.id.customiseMoveIV);

        return inflate;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        constraintLayout.post(
                () -> {
                    Rect offsetViewBounds = new Rect();
                    cardView.getDrawingRect(offsetViewBounds);
                    constraintLayout.offsetDescendantRectToMyCoords(cardView, offsetViewBounds);

                    float cardX = sharedPreferenceHelper.getCardX();
                    float cardY = sharedPreferenceHelper.getCardY();

                    if (cardX != -1)
                        cardView.setX(
                                (constraintLayout.getWidth() - offsetViewBounds.left) / cardX);
                    if (cardY != -1)
                        cardView.setY(
                                (constraintLayout.getHeight() - offsetViewBounds.top) / cardY);
                });

        AtomicReference<Float> dX = new AtomicReference<>(cardView.getX());
        AtomicReference<Float> dY = new AtomicReference<>(cardView.getY());

        moveIV.setOnTouchListener(
                (v, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX.set(cardView.getX() + v.getX() - event.getRawX());
                            dY.set(cardView.getY() + v.getY() - event.getRawY());
                            break;

                        case MotionEvent.ACTION_MOVE:
                            cardView.setX(event.getRawX() + dX.get());
                            cardView.setY(event.getRawY() + dY.get());

                            break;

                        case MotionEvent.ACTION_UP:
                            int[] array = new int[2];
                            cardView.getLocationOnScreen(array);

                            sharedPreferenceHelper.setCardX(
                                    constraintLayout.getWidth() / (float) array[0]);
                            sharedPreferenceHelper.setCardY(
                                    constraintLayout.getHeight() / (float) array[1]);

                            break;

                        default:
                            return false;
                    }
                    return true;
                });
    }
}
