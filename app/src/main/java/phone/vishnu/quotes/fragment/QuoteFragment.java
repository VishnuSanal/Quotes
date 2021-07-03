package phone.vishnu.quotes.fragment;

import android.app.Application;
import android.graphics.Color;
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
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.ShareHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class QuoteFragment extends Fragment {

    private Quote quote;
    private ImageView shareIcon, favIcon;
    private TextView quoteText, authorText;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public QuoteFragment() {
    }

    public static QuoteFragment newInstance(Quote quote) {

        QuoteFragment fragment = new QuoteFragment();

        Bundle bundle = new Bundle();
        bundle.putString("quote", quote.getQuote());
        bundle.putString("author", quote.getAuthor());
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View quoteView = inflater.inflate(R.layout.fragment_quote, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        quoteText = quoteView.findViewById(R.id.quoteTextView);
        authorText = quoteView.findViewById(R.id.authorTextView);
        shareIcon = quoteView.findViewById(R.id.shareImageView);
        favIcon = quoteView.findViewById(R.id.favoriteImageView);

        shareIcon.setImageDrawable(
                ShareHelper.getShareIconDrawable(
                        requireContext(),
                        sharedPreferenceHelper.getShareButtonAction()
                )
        );

        String hexColor = sharedPreferenceHelper.getCardColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();

        if ((!fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                quoteText.setTypeface(face);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Font file not found", Toast.LENGTH_SHORT).show();
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        } else {
            if ((!fontPath.equals("-1")))
                if (!new File(fontPath).exists())
                    Toast.makeText(requireContext(), "Font file not found", Toast.LENGTH_SHORT).show();
        }

        quoteText.setTextColor(Color.parseColor(fontColor));
        authorText.setTextColor(Color.parseColor(fontColor));

        ((CardView) quoteView.findViewById(R.id.cardView)).setCardBackgroundColor(Color.parseColor(hexColor));
        authorText.setBackgroundColor(Color.parseColor(hexColor));

        //noinspection ConstantConditions
        quote = new Quote(
                getArguments().getString("quote"),
                getArguments().getString("author")
        );

        quoteText.setText(quote.getQuote());
        authorText.setText(String.format("-%s", quote.getAuthor()));

        try {

            FavRepository repository = new FavRepository((Application) requireContext().getApplicationContext());

            if (repository.isPresent(quote))
                favIcon.setColorFilter(Color.RED);

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        return quoteView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shareIcon.setOnClickListener(v -> {

            final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
            shareIcon.startAnimation(shake);
            shareIcon.setColorFilter(getResources().getColor(R.color.favGreenColor));

            shareButtonClicked(sharedPreferenceHelper.getShareButtonAction(),
                    new Quote(quoteText.getText().toString(), authorText.getText().toString())
            );
        });

        favIcon.setOnClickListener(v -> {
            final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
            favIcon.startAnimation(shake);

            FavRepository repository = new FavRepository((Application) requireContext().getApplicationContext());

            long l = repository.insertFav(quote);

            if (l == -1) {
                repository.deleteFav(quote);
                favIcon.setColorFilter(Color.WHITE);
            } else
                favIcon.setColorFilter(getResources().getColor(R.color.favRedColor));

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