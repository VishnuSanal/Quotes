package phone.vishnu.quotes.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog;
import com.turkialkhateeb.materialcolorpicker.ColorListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;
import phone.vishnu.quotes.R;

public class SplashActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences sharedPreferences = getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

        String FIRST_RUN_BOOLEAN = "firstRunPreference";
        if (sharedPreferences.getBoolean(FIRST_RUN_BOOLEAN, true)) {
            showTour();
        } else {
            initTasks();
        }
    }

    private void showTour() {
        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        /*Screen 1*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_quotes)
                .title("Spread positivity with us")
                .description("Would you try?")
                .build());

        /*Screen 2*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_quotes_round)
                .title("It Works Like........")
                .description("Share Quotations from World Leaders as an Image that too without any hassles of image editing......")
                .build());

        /*Hints*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_unfold_more)
                .title("Scroll For More")
                .description("Scroll horizontally to get more awesome Quotes")
                .build());

        /*Permission Screen*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_check_box)
                .neededPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET})
                .title("Accept Permissions")
                .description("To make it clear......\n Permission for Internet is to load Quotes \n Permission for External Storage is to store Image Files temporarily")
                .build());

        /*BG Image*/
        //FIXME:There's an error here
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_insert_photo)
                .title("Background image")
                .description("You can select a custom background image for the app from the overflow menu on the bottom of the screen")
                .build()/*, new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().add(R.id.constraintLayout, PickFragment.newInstance()).addToBackStack(null).commit();
            }
        }, "Choose Image")*/);

        /*Accent Color*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.color.cardBackgroundColor)
                .title("Choose accent color")
                .description("Would you like to select a custom accent color for the app? Do nothing to use the above default accent color. You can change this later from the overflow menu on the bottom of the screen")
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String COLOR_PREFERENCE_NAME = "colorPreference";

                final SharedPreferences prefs = SplashActivity.this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);

                ColorChooserDialog dialog = new ColorChooserDialog(SplashActivity.this);
                dialog.setTitle("Choose Color");
                dialog.setColorListener(new ColorListener() {
                    @Override
                    public void OnColorClick(View v, int color) {

                        String colorString = Integer.toHexString(color).substring(2);

                        //TODO:Needs Fixing of string "WHITE"
                        if (colorString.toLowerCase().equals("ffffff")) colorString = "00000000";

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(COLOR_PREFERENCE_NAME, "#" + colorString);
                        editor.apply();
                    }
                });
                dialog.show();
            }
        }, "Choose Color"));

        /*Notification*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_notifications)
                .title("Daily Notification of Quotes")
                .description("You will receive daily notifications with Quotes at 08:30 AM. You can customise this from the about screen")
                .build());

        /*Share*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_share)
                .title("Share Quotes in Social Media")
                .description("Click share icon from a Quote and select to share that Quote in the form of an Image")
                .build());

        /*Fav*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_favorite)
                .title("Add a Quote to Favorites")
                .description("Click favourite icon from a quote. You can view the favorite quotes by clicking on the overflow menu on the bottom of the screen")
                .build());

        /*End Screen*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .title("That's it")
                .image(R.drawable.ic_whatshot)
                .description("Get Started")
                .build());
    }

    @Override
    public void onFinish() {
        super.onFinish();
        String FIRST_RUN_BOOLEAN = "firstRunPreference";
        SharedPreferences sharedPreferences = getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(FIRST_RUN_BOOLEAN, false).apply();

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        SplashActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int PICK_IMAGE_ID = 22;
        if ((requestCode == PICK_IMAGE_ID) && (resultCode == Activity.RESULT_OK)) {
            if (data != null) {
                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());

                    String file = generateNoteOnSD(bitmap);

                    SharedPreferences sharedPrefs = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
                    editor.putString(BACKGROUND_PREFERENCE_NAME, file);
                    editor.apply();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initTasks() {
        setContentView(R.layout.activity_splash);
        final String COLOR_PREFERENCE_NAME = "colorPreference";

        String colorString = this.getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE)
                .getString(COLOR_PREFERENCE_NAME, "#5C5C5C");

        if (colorString.equals("#00000000")) colorString = "#5C5C5C";

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

    private String generateNoteOnSD(Bitmap image) {
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
        return file;
    }
}