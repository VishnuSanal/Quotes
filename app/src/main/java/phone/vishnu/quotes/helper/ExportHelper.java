package phone.vishnu.quotes.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;

public class ExportHelper {

    private final SharedPreferenceHelper sharedPreferenceHelper;

    public ExportHelper(Context context) {
        sharedPreferenceHelper = new SharedPreferenceHelper(context);
    }

    public void exportBackgroundImage(Bitmap image) {
        try {

            FileOutputStream fOutputStream = new FileOutputStream(getBGPath());
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            fOutputStream.flush();
            fOutputStream.close();

            sharedPreferenceHelper.setBackgroundPath(getBGPath());

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    public String getBGPath() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), (BuildConfig.DEBUG) ? "Quotes - Debug" : "Quotes");
        if (!root.exists()) root.mkdirs();
        return root.toString() + File.separator + ".Quotes_Background" + ".jpg";
    }

    public String getSSPath() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), (BuildConfig.DEBUG) ? "Quotes - Debug" : "Quotes");
        if (!root.exists()) root.mkdirs();
        return root.toString() + File.separator + ".Screenshot" + ".jpg";
    }

    public void shareImage(Context context, Quote q) {

        String quote = q.getQuote();
        String author = q.getAuthor();

        View shareView = View.inflate(context, R.layout.share_layout, null);

        String cardColor = sharedPreferenceHelper.getCardColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();

        String backgroundPath = sharedPreferenceHelper.getBackgroundPath();
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(cardColor));

        TextView shareQuoteTextView = shareView.findViewById(R.id.shareQuoteTextView);
        TextView shareAuthorTextView = shareView.findViewById(R.id.shareAuthorTextView);

        if (!(fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                shareQuoteTextView.setTypeface(face);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        shareQuoteTextView.setTextColor(Color.parseColor(fontColor));
        shareAuthorTextView.setTextColor(Color.parseColor(fontColor));

        shareQuoteTextView.setText(quote);
        shareAuthorTextView.setText(author);

        int widthPixels = 1080;
        int heightPixels = 1920;

        shareView.measure(View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(heightPixels, View.MeasureSpec.EXACTLY));

        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        shareView.layout(0, 0, widthPixels, heightPixels);
        shareView.draw(c);

        String imagePath = getSSPath();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException | SecurityException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(imagePath));
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void saveImage(Context context, Quote q) {

        String quote = q.getQuote();
        String author = q.getAuthor();

        View shareView = View.inflate(context, R.layout.share_layout, null);

        String cardColor = sharedPreferenceHelper.getCardColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();

        String backgroundPath = sharedPreferenceHelper.getBackgroundPath();
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(cardColor));

        TextView shareQuoteTextView = shareView.findViewById(R.id.shareQuoteTextView);
        TextView shareAuthorTextView = shareView.findViewById(R.id.shareAuthorTextView);

        if (!(fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                shareQuoteTextView.setTypeface(face);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        shareQuoteTextView.setTextColor(Color.parseColor(fontColor));
        shareAuthorTextView.setTextColor(Color.parseColor(fontColor));

        shareQuoteTextView.setText(quote);
        shareAuthorTextView.setText(author);

        int widthPixels = 1080;
        int heightPixels = 1920;

        shareView.measure(View.MeasureSpec.makeMeasureSpec(widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(heightPixels, View.MeasureSpec.EXACTLY));

        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        shareView.layout(0, 0, widthPixels, heightPixels);
        shareView.draw(c);

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), (BuildConfig.DEBUG) ? "Quotes - Debug" : "Quotes");
        if (!root.exists()) root.mkdirs();
        String imagePath = root.toString() + File.separator + "Quotes - " + System.currentTimeMillis() + ".jpg";

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException | SecurityException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context, new String[]{imagePath}, null, null);
    }
}
