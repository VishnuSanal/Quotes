package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.FavUtils;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.Quote;

public class QuoteFragment extends Fragment {

    private Quote quote;
    private FavUtils favUtils;
    private ExportHelper exportHelper;
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

        shareIcon.setImageDrawable(getShareIconDrawable(sharedPreferenceHelper.getShareButtonAction()));

        favUtils = new FavUtils(requireContext());
        exportHelper = new ExportHelper(requireContext());

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

        quote = new Quote(
                Objects.requireNonNull(getArguments()).getString("quote"),
                Objects.requireNonNull(getArguments()).getString("author")
        );

        quoteText.setText(quote.getQuote());
        authorText.setText(String.format("-%s", quote.getAuthor()));

        try {
            if (favUtils.isPresent(quote))
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

        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                shareIcon.startAnimation(shake);
                shareIcon.setColorFilter(Color.GREEN);

                shareButtonClicked(sharedPreferenceHelper.getShareButtonAction(),
                        new Quote(quoteText.getText().toString(), authorText.getText().toString())
                );
            }
        });

        favIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.animate);
                favIcon.startAnimation(shake);

                if (!favUtils.newFavorite(quote))
                    favIcon.setColorFilter(Color.RED);
                else
                    favIcon.setColorFilter(Color.WHITE);
            }
        });
    }

    private Drawable getShareIconDrawable(int i) {

        //Copy -> 0
        //Share -> 1
        //Save -> 2

        if (i == 0)
            return ContextCompat.getDrawable(requireContext(), R.drawable.ic_copy);
        else if (i == 1)
            return ContextCompat.getDrawable(requireContext(), R.drawable.ic_share);
        else if (i == 2)
            return ContextCompat.getDrawable(requireContext(), R.drawable.ic_save);

        return ContextCompat.getDrawable(requireContext(), R.drawable.ic_share);
    }

    private void shareButtonClicked(int i, Quote q) {

        //Copy -> 0
        //Share -> 1
        //Save -> 2

        if (i == 0) {
            copyQuote(q);
        } else if (i == 1) {
            shareQuote(q);
        } else if (i == 2) {
            saveQuote(q);
        }
    }

    private void copyQuote(Quote quote) {

        String q = "\"" + quote.getQuote() + "\"" + " - " + quote.getAuthor().replace("-", "");

        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getResources().getString(R.string.app_name), q);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(requireContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
    }

    private void saveQuote(final Quote q) {

        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(requireContext(), "Saving to Gallery", Toast.LENGTH_SHORT).show();
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                exportHelper.saveImage(requireContext(), q);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(requireContext(), "App requires these permissions to share the quote", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    private void shareQuote(final Quote q) {
        Dexter.withContext(requireContext())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                exportHelper.shareImage(requireContext(), q);
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(requireContext(), "App requires these permissions to share the quote", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                startActivity(
                        new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", requireContext().getPackageName(), null))
                );
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface imageDialog, int which) {
                imageDialog.cancel();
                Toast.makeText(requireContext(), "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();

    }
}