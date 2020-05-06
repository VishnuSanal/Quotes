package phone.vishnu.quotes.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.data.QuoteViewPagerAdapter;
import phone.vishnu.quotes.fragment.BlankFragment;
import phone.vishnu.quotes.fragment.BottomSheetFragment;
import phone.vishnu.quotes.fragment.FavoriteFragment;
import phone.vishnu.quotes.fragment.QuoteFragment;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.receiver.NotificationReceiver;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {
    private static final int PICK_IMAGE_ID = 36;
    private final String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private QuoteViewPagerAdapter adapter;
    private int PERMISSION_REQ_CODE = 88;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (savedInstanceState == null) {
            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotificationClick")) {
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            shareScreenshot(extras.getString("quote"), extras.getString("author"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        }


        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraintLayout);
        viewPager = findViewById(R.id.viewPager);

        ImageView menuIcon = findViewById(R.id.homeMenuIcon);
/*
        final String[] backgroundPath = {this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).getString(BACKGROUND_PREFERENCE_NAME, "-1")};

        if (("-1".equals(backgroundPath[0]))) {

            final ProgressDialog dialog = ProgressDialog.show(this,"","Loading...");

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    backgroundPath[0] = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).getString(BACKGROUND_PREFERENCE_NAME, "-1");

                    Log.e("vishnu", "run: " );

                    if (!"-1".equals(backgroundPath[0])){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                constraintLayout.setBackground(Drawable.createFromPath(backgroundPath[0]));
                                dialog.dismiss();
                                Log.e("vishnu", "run: if: " );
                            }
                        });
                    }
                }
            }, 1000, 1000);
        }
        */

        String backgroundPath = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).getString(BACKGROUND_PREFERENCE_NAME, "-1");
        if (!"-1".equals(backgroundPath))
            constraintLayout.setBackground(Drawable.createFromPath(backgroundPath));

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetTag");
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), getFragments());
                viewPager.setAdapter(adapter);
            }
        });

        myAlarm();
    }

    private void myAlarm() {

        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    private List<Fragment> getFragments() {

        final List<Fragment> fragments = new ArrayList<>();
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {

                Collections.shuffle(quotes);

                for (int i = 0; i < quotes.size(); i++) {
                    QuoteFragment quoteFragment = QuoteFragment.newInstance(quotes.get(i).getQuote(), quotes.get(i).getAuthor());
                    fragments.add(quoteFragment);
                }

                adapter.notifyDataSetChanged();
            }
        });
        return fragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == PICK_IMAGE_ID) && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    String file = generateNoteOnSD(this, bitmap);

                    constraintLayout.setBackground(Drawable.createFromPath(file));

                    SharedPreferences sharedPrefs = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                    editor.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String generateNoteOnSD(Context context, Bitmap image) {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Quotes");

        if (!root.exists()) root.mkdirs();

        String file = root.toString() + File.separator + ".Quotes_Background" + ".jpg";

        try {
            FileOutputStream fOutputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fOutputStream);

            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            fOutputStream.flush();
            fOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaScannerConnection.scanFile(context, new String[]{file}, null, null);
        return file;
    }

    @Override
    public void onButtonClicked(int id) {
        switch (id) {
            case R.id.bottomSheetFav: {
                FavoriteFragment fragment = FavoriteFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.bottomSheetAbout: {
                BlankFragment fragment = BlankFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.bottomSheetImageChooser: {

                if (!isPermissionGranted())
                    isPermissionGranted();
                else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_IMAGE_ID);
                }
                break;
            }
            case R.id.bottomSheetColorChooser: {
                final String COLOR_PREFERENCE_NAME = "colorPreference";

                final SharedPreferences prefs = MainActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

                ColorChooserDialog dialog = new ColorChooserDialog(MainActivity.this);
                dialog.setTitle("Choose Color");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(COLOR_PREFERENCE_NAME, "#" + Integer.toHexString(color).substring(2));
                        editor.apply();
                        MainActivity.this.recreate();
                    }
                });
                dialog.show();

                break;

            }

        }
    }

    private void showPermissionDeniedDialog() {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Permission to Capture Screenshot of the Screen");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 22) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showPermissionDeniedDialog();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQ_CODE);
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private void shareScreenshot(String quote, String author) {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View shareView = inflater.inflate(R.layout.share_layout, null);

        SharedPreferences sharedPreferences = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        String hexColor = sharedPreferences.getString("colorPreference", "#5C5C5C");

        String backgroundPath = sharedPreferences.getString("backgroundPreference", "-1");
        if (!"-1".equals(backgroundPath))
            shareView.findViewById(R.id.shareRelativeLayout).setBackground(Drawable.createFromPath(backgroundPath));

        CardView cardView = shareView.findViewById(R.id.shareCardView);
        cardView.setCardBackgroundColor(Color.parseColor(hexColor));

        ((ImageView) shareView.findViewById(R.id.shareFavoriteImageView)).setColorFilter(Color.RED);
        ((ImageView) shareView.findViewById(R.id.shareShareImageView)).setColorFilter(Color.GREEN);

        ((TextView) shareView.findViewById(R.id.shareQuoteTextView)).setText(quote);
        ((TextView) shareView.findViewById(R.id.shareAuthorTextView)).setText(author);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

        shareView.measure(View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.AT_MOST));

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
            e.printStackTrace();
        }

        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", new File(imagePath));
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
