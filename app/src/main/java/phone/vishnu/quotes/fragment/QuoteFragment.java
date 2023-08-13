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

import android.app.Application;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import java.io.File;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class QuoteFragment extends Fragment {

    private Quote quote;
    private CardView cardView;
    private ImageView shareIcon, favIcon;
    private TextView quoteText, authorText;
    private ConstraintLayout constraintLayout;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public QuoteFragment() {}

    public static QuoteFragment newInstance(Quote quote) {

        QuoteFragment fragment = new QuoteFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.QUOTE, quote.getQuote());
        bundle.putString(Constants.AUTHOR, quote.getAuthor());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        quoteText = quoteView.findViewById(R.id.quoteTextView);
        authorText = quoteView.findViewById(R.id.authorTextView);
        shareIcon = quoteView.findViewById(R.id.shareImageView);
        favIcon = quoteView.findViewById(R.id.favoriteImageView);
        cardView = quoteView.findViewById(R.id.cardView);
        constraintLayout = quoteView.findViewById(R.id.quoteFragmentContainer);

        shareIcon.setImageDrawable(
                ShareHelper.getShareIconDrawable(
                        requireContext(), sharedPreferenceHelper.getShareButtonAction()));

        String hexColor = sharedPreferenceHelper.getCardColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();
        float fontSize = sharedPreferenceHelper.getFontSizePreference();
        //        float cardX = sharedPreferenceHelper.getCardX();
        //        float cardY = sharedPreferenceHelper.getCardY();

        if ((!fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                quoteText.setTypeface(face);
            } catch (Exception e) {
                Toast.makeText(
                                getContext(),
                                getString(R.string.font_file_not_found),
                                Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            }
        } else {
            if ((!fontPath.equals("-1")))
                if (!new File(fontPath).exists())
                    Toast.makeText(
                                    requireContext(),
                                    getString(R.string.font_file_not_found),
                                    Toast.LENGTH_SHORT)
                            .show();
        }

        quoteText.setTextColor(Color.parseColor(fontColor));
        authorText.setTextColor(Color.parseColor(fontColor));

        quoteText.setTextSize(fontSize);
        authorText.setTextSize((float) (fontSize / 1.2));

        cardView.setCardBackgroundColor(Color.parseColor(hexColor));
        authorText.setBackgroundColor(Color.parseColor(hexColor));

        // noinspection ConstantConditions
        quote =
                new Quote(
                        getArguments().getString(Constants.QUOTE),
                        getArguments().getString(Constants.AUTHOR));

        quoteText.setText(quote.getQuote());
        authorText.setText(String.format("-%s", quote.getAuthor()));

        try {

            FavRepository repository =
                    new FavRepository((Application) requireContext().getApplicationContext());

            if (repository.isPresent(quote)) favIcon.setColorFilter(Color.RED);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return quoteView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        shareIcon.setOnClickListener(
                v -> {
                    final Animation shake =
                            AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                    shareIcon.startAnimation(shake);
                    shareIcon.setColorFilter(getResources().getColor(R.color.favGreenColor));

                    shareButtonClicked(
                            sharedPreferenceHelper.getShareButtonAction(),
                            new Quote(
                                    quoteText.getText().toString(),
                                    authorText.getText().toString()));
                });

        favIcon.setOnClickListener(
                v -> {
                    final Animation shake =
                            AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                    favIcon.startAnimation(shake);

                    FavRepository repository =
                            new FavRepository(
                                    (Application) requireContext().getApplicationContext());

                    long l = repository.insertFav(quote);

                    if (l == -1) {
                        repository.deleteFav(quote);
                        favIcon.setColorFilter(Color.WHITE);
                    } else favIcon.setColorFilter(getResources().getColor(R.color.favRedColor));
                });
    }

    private void shareButtonClicked(int i, Quote q) {
        if (i == 0) {
            ShareHelper.copyQuote(requireContext(), q);
        } else if (i == 1) {
            ShareHelper.shareQuote(requireContext(), q);
        } else if (i == 2) {
            ShareHelper.saveQuote(requireContext(), q);
        } else if (i == 3) {
            showBottomSheetDialog(q);
        }
    }

    private void showBottomSheetDialog(Quote q) {
        ShareOptionPickFragment bottomSheet = ShareOptionPickFragment.newInstance(q);
        bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalBottomSheet");
    }
}
