package phone.vishnu.quotes.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.TourFragmentStateAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        removeFonts();

        if (sharedPreferenceHelper.isFirstRun())
            showNewTour();
        else
            initTasks();
    }

    private void removeFonts() {
        sharedPreferenceHelper.deleteFontPreference();

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), (BuildConfig.DEBUG) ? "Quotes - Debug" : "Quotes");
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
        setContentView(R.layout.activity_tour);

        final ViewPager2 viewPager = findViewById(R.id.splashScreenTourViewPager);

        final TourFragmentStateAdapter adapter = new TourFragmentStateAdapter(this);
        viewPager.setAdapter(adapter);

        final int pageCount = adapter.getItemCount() - 1;

        findViewById(R.id.splashScreenNextButton).setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pageCount)
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            else {
                tourCompleted();
            }
        });
        findViewById(R.id.splashScreenBackButton).setOnClickListener(v -> viewPager.setCurrentItem(viewPager.getCurrentItem() - 1));
        findViewById(R.id.splashScreenSkipButton).setOnClickListener(v -> tourCompleted());
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if ((position == pageCount))
                    tourCompleted();
            }
        });
    }

    private void tourCompleted() {
        sharedPreferenceHelper.setFirstRunBoolean(false);

        Dexter.withContext(this)
                .withPermissions(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        moveToNext();
                    }

                    @Override
                    public void onPermissionDenied(final PermissionDeniedResponse permissionDeniedResponse) {
                        showPermissionDeniedDialog();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        Toast.makeText(SplashActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void showPermissionDeniedDialog() {
        final androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("Please Accept Necessary Permissions");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", (imageDialog, which) -> {
            imageDialog.cancel();
            startActivity(
                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            .setData(Uri.fromParts("package", getPackageName(), null))
            );
        });
        builder.setNegativeButton("Cancel", (imageDialog, which) -> {
            imageDialog.cancel();
            Toast.makeText(SplashActivity.this, "App requires these permissions to run properly", Toast.LENGTH_SHORT).show();
        });
        builder.show();

    }

    private void moveToNext() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    private void initTasks() {
        setContentView(R.layout.activity_splash);

        String colorString = sharedPreferenceHelper.getCardColorPreference();

        if (colorString.equals("#00000000")) colorString = "#607D8B";

        if (BuildConfig.DEBUG)
            ((TextView) findViewById(R.id.splashScreenAppNameTextView)).setText("Quotes Debug");

        ((TextView) findViewById(R.id.splashScreenAppNameTextView)).setTextColor(Color.parseColor(colorString));

        int SPLASH_TIMEOUT = 1;
        new Handler().postDelayed(this::moveToNext, SPLASH_TIMEOUT * 1000);
    }
}