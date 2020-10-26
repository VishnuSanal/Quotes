package phone.vishnu.quotes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.helper.TourFragmentStateAdapter;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        if (sharedPreferenceHelper.isFirstRun())
            showNewTour();
        else
            initTasks();
    }

    private void showNewTour() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_tour);

        final ViewPager2 viewPager = findViewById(R.id.splashScreenTourViewPager);

        final TourFragmentStateAdapter adapter = new TourFragmentStateAdapter(this);
        viewPager.setAdapter(adapter);

        findViewById(R.id.splashScreenNextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewPager.getCurrentItem() < adapter.getItemCount() - 1)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                else {
                    tourCompleted();
                }
            }
        });
        findViewById(R.id.splashScreenBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
        findViewById(R.id.splashScreenSkipButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tourCompleted();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == adapter.getItemCount() - 1)
                    tourCompleted();
            }
        });
    }

    private void tourCompleted() {
        sharedPreferenceHelper.setFirstRunBoolean(false);
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    private void initTasks() {
        setContentView(R.layout.activity_splash);

        String colorString = sharedPreferenceHelper.getColorPreference();

        if (colorString.equals("#00000000")) colorString = "#607D8B";

        ((TextView) findViewById(R.id.splashScreenAppNameTextView)).setTextColor(Color.parseColor(colorString));

        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }
        }, SPLASH_TIMEOUT * 1000);
    }

}