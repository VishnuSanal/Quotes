package phone.vishnu.quotes.activity;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.data.QuoteViewPagerAdapter;
import phone.vishnu.quotes.fragment.BlankFragment;
import phone.vishnu.quotes.fragment.QuoteFragment;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.receiver.AlarmReceiver;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView tv;
    QuoteViewPagerAdapter adapter;
    String message, author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tv = findViewById(R.id.tv);

        setUpNotification();

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BlankFragment fragment = BlankFragment.newInstance();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.constraintLayout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void setUpNotification() {


        new QuoteData().getQuotes(new QuoteListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Quote> quotes) {

                Calendar calendar = Calendar.getInstance();
//                calendar.setTimeInMillis(System.currentTimeMillis());

                calendar.set(Calendar.HOUR_OF_DAY, 7);
                calendar.set(Calendar.MINUTE, 30);
                calendar.set(Calendar.SECOND, 0);

                Collections.shuffle(quotes);

                message = quotes.get(0).getQuote();
                author = quotes.get(0).getAuthor();

                Intent intent = new Intent(MainActivity.this, AlarmReceiver.class);

                intent.putExtra("message", message);
                intent.putExtra("author", author);

                AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(ALARM_SERVICE);

                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, calendar.getTimeInMillis(), (AlarmManager.INTERVAL_DAY / 4), null);
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
}
