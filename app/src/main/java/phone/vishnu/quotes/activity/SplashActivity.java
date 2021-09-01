/*
 * Copyright (C) 2019 - 2019-2021 Vishnu Sanal. T
 *
 * This file is part of Quotes Status Creator.
 *
 * Quotes Status Creator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package phone.vishnu.quotes.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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

        sharedPreferenceHelper = new SharedPreferenceHelper(this);
        checkTheme(sharedPreferenceHelper.getAppThemePreference());

        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_splash);

        removeFonts();

        if (!sharedPreferenceHelper.isFirstRun() && sharedPreferenceHelper.isNewFirstRun())
            sharedPreferenceHelper.resetSharedPreferences();

        if (sharedPreferenceHelper.isNewFirstRun() || sharedPreferenceHelper.isFirstRun())
            showNewTour();
        else initTasks();
    }

    private void checkTheme(int i) {
        if (i == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (i == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

    private void removeFonts() {
        sharedPreferenceHelper.deleteFontPreference();

        File root =
                new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOCUMENTS),
                        getString(R.string.app_name));

        File[] files = root.listFiles();

        ArrayList<String> arrayList = sharedPreferenceHelper.getFontListToBeRemoved();

        if (files != null) {
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".ttf")) {
                    if (arrayList.contains(file.getName())) file.delete();
                }
            }
        }
    }

    private void showNewTour() {
        getWindow()
                .setFlags(
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.splashScreenConstraintLayout, TourFragment.newInstance())
                .addToBackStack(null)
                .commit();
    }

    public void moveToNext() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    private void initTasks() {

        String colorString = sharedPreferenceHelper.getCardColorPreference();

        if (colorString.equals("#00000000")) colorString = "#607D8B";

        ((TextView) findViewById(R.id.splashScreenAppNameTextView))
                .setTextColor(Color.parseColor(colorString));

        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(this::moveToNext, SPLASH_TIMEOUT * 1000);
    }
}
