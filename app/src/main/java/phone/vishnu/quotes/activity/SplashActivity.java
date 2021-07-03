package phone.vishnu.quotes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.fragment.TourFragment;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        removeFonts();

        if (!sharedPreferenceHelper.isFirstRun() && sharedPreferenceHelper.isNewFirstRun())
            sharedPreferenceHelper.resetSharedPreferences();

        if (sharedPreferenceHelper.isNewFirstRun() || sharedPreferenceHelper.isFirstRun())
            showNewTour();
        else
            initTasks();

    }

    private void removeFonts() {
        sharedPreferenceHelper.deleteFontPreference();

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                getString(R.string.app_name));

        File[] files = root.listFiles();

        ArrayList<String> arrayList = sharedPreferenceHelper.getFontListToBeRemoved();

        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".ttf")) {
                    if (arrayList.contains(file.getName()))
                        file.delete();
                }
            }
        }
    }

    private void showNewTour() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.splashScreenConstraintLayout, TourFragment.newInstance())
                .addToBackStack(null).commit();

    }

    public void moveToNext() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    private void initTasks() {

        String colorString = sharedPreferenceHelper.getCardColorPreference();

        if (colorString.equals("#00000000")) colorString = "#607D8B";

        ((TextView) findViewById(R.id.splashScreenAppNameTextView)).setTextColor(Color.parseColor(colorString));

        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(this::moveToNext, SPLASH_TIMEOUT * 1000);
    }
}