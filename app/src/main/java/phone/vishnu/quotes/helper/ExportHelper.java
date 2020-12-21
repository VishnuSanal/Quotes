package phone.vishnu.quotes.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");
        if (!root.exists()) root.mkdirs();
        return root.toString() + File.separator + ".Quotes_Background" + ".jpg";
    }

    public void shareScreenshot(Context context, Quote q) {

        String quote = q.getQuote();
        String author = q.getAuthor();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams") View shareView = inflater.inflate(R.layout.share_layout, null);

        String hexColor = sharedPreferenceHelper.getColorPreference();
        String fontColor = sharedPreferenceHelper.getFontColorPreference();
        String fontPath = sharedPreferenceHelper.getFontPath();

        String backgroundPath = sharedPreferenceHelper.getBackgroundPath();
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(hexColor));

        if (!(fontPath.equals("-1")) && (new File(fontPath).exists())) {
            try {
                Typeface face = Typeface.createFromFile(fontPath);
                ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setTypeface(face);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setTextColor(Color.parseColor(fontColor));
        ((TextView) shareView.findViewById(R.id.shareAuthorTextView)).setTextColor(Color.parseColor(fontColor));

        ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setText(quote);
        ((TextView) shareView.findViewById(R.id.shareAuthorTextView)).setText(author);

        DisplayMetrics metrics = new DisplayMetrics();
        metrics.widthPixels = 1080;
        metrics.heightPixels = 1920;

        shareView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.EXACTLY));

        shareView.findViewById(R.id.shareRelativeLayout).setLayoutParams(new LinearLayout.LayoutParams(metrics.widthPixels, metrics.heightPixels));

        shareView.setDrawingCacheEnabled(true);

        Bitmap bitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.heightPixels, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        shareView.layout(0, 0, metrics.widthPixels, metrics.heightPixels);
        shareView.draw(c);

        shareView.buildDrawingCache(true);

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");
        if (!root.exists()) root.mkdirs();
        String imagePath = root.toString() + File.separator + ".Screenshot" + ".jpg";
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
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
}
