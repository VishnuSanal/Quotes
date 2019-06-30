package phone.vishnu.quotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import phone.vishnu.quotes.data.QuoteData;
import phone.vishnu.quotes.data.QuoteListAsyncResponse;
import phone.vishnu.quotes.data.QuoteViewPagerAdapter;
import phone.vishnu.quotes.model.Quote;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    TextView tv;
    QuoteViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new QuoteViewPagerAdapter(getSupportFragmentManager(), getFragments());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        tv=findViewById(R.id.tv);


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


    public List<Fragment> getFragments() {

        final List<Fragment> fragments = new ArrayList<>();

        new QuoteData().getQuotes(new QuoteListAsyncResponse() {

            @Override
            public void processFinished(ArrayList<Quote> quotes) {

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
