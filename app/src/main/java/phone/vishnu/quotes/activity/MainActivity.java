package phone.vishnu.quotes.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

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
    private static final int PICK_IMAGE_ID = 22;
    private static final String PREFERENCE_NAME = "backgroundPreference";
    private ConstraintLayout constraintLayout;
    private ViewPager viewPager;
    private QuoteViewPagerAdapter adapter;
//    private String message = "Quote not found", author = "Author not found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.constraintLayout);
        viewPager = findViewById(R.id.viewPager);
        ImageView menuIcon = findViewById(R.id.homeMenuIcon);

        String backgroundPath = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).getString(PREFERENCE_NAME, "-1");

        if (!("-1".equals(backgroundPath)))
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
                    editor.putString(PREFERENCE_NAME, file);
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
        if (id == R.id.bottomSheetFav) {
            FavoriteFragment fragment = FavoriteFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.bottomSheetAbout) {
            BlankFragment fragment = BlankFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.constraintLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.bottomSheetImageChooser) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_ID);
        }
    }
}
