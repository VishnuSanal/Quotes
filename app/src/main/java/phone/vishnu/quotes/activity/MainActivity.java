package phone.vishnu.quotes.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import phone.vishnu.quotes.receiver.AlarmReceiver;

public class MainActivity extends AppCompatActivity implements BottomSheetFragment.BottomSheetListener {
    ImageView menuIcon;
    private ViewPager viewPager;
    private TextView aboutTV, favoriteTV;
    private QuoteViewPagerAdapter adapter;
    private String message = "Quote not found", author = "Author not found";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        menuIcon = findViewById(R.id.homeMenuIcon);
//        aboutTV = findViewById(R.id.aboutTextView);
//        favoriteTV = findViewById(R.id.favoritesTextView);

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
        SharedPreferences settings = getSharedPreferences("phone.vishnu.quotes.activity.isNotificationSetPrefs", MODE_PRIVATE); // Get preferences file (0 = no option flags set)
        boolean isNotificationSet = settings.getBoolean("isNotificationSet", false); // Is it first run? If not specified, use "true"

        if (!isNotificationSet) {
            SharedPreferences.Editor editor = settings.edit(); // Open the editor for our settings
            editor.putBoolean("isNotificationSet", true); // It is no longer the first run
            editor.apply(); // Save all changed settings
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    setUpNotification();
                }
            });
        }

    }

    private void setUpNotification() {
        new QuoteData().getQuotes(new QuoteListAsyncResponse() {

            @Override
            public void processFinished(ArrayList<Quote> quotes) {

                final long INTERVAL = (AlarmManager.INTERVAL_DAY / 3);
                final int REQ_CODE = 2222;
                Calendar cal = Calendar.getInstance();
//                cal.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DATE)+1);
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.HOUR_OF_DAY, 7);
                cal.set(Calendar.MINUTE, 30);
                cal.add(Calendar.SECOND, 0);

                Collections.shuffle(quotes);
                message = quotes.get(0).getQuote();
                author = quotes.get(0).getAuthor();

                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);
                intent.putExtra("message", message);
                intent.putExtra("author", author);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + INTERVAL, INTERVAL, pendingIntent);
            }
        });
    }

    public List<Fragment> getFragments() {

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
        }
    }
}
