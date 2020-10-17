package phone.vishnu.quotes.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.FloatRange;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.receiver.NotificationReceiver;

public class SplashActivity extends MaterialIntroActivity {

    private SharedPreferenceHelper sharedPreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sharedPreferenceHelper = new SharedPreferenceHelper(this);

        if (sharedPreferenceHelper.isFirstRun()) {
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
                .build());

        /*Accent Color*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_color_lens)
                .title("Custom accent color")
                .description("You can select a custom accent colour for the app from the overflow menu on the bottom of the screen")
                .build());

        /*Font FamilyColor*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_font)
                .title("Custom font")
                .description("You can select a custom font for the app from the overflow menu on the bottom of the screen")
                .build());

        /*Notification*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_notifications)
                .title("Daily Notification of Quotes")
                .description("You will receive daily notifications with Quotes at 08:30 AM. You can change the time here. You can customise this later from the about screen")
                .build(), new MessageButtonBehaviour(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final Calendar c = Calendar.getInstance();

                TimePickerDialog timePicker = new TimePickerDialog(SplashActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        c.set(Calendar.MINUTE, minute);

                        sharedPreferenceHelper.setAlarmString("At " + hourOfDay + " : " + minute + " Daily");

                        myAlarm(c);

                    }
                }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(SplashActivity.this));
                timePicker.show();


            }
        }, "Set"));

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

        /*Custom Quotes*/
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.tourBackgroundColor)
                .buttonsColor(R.color.tourButtonColor)
                .image(R.drawable.ic_post_add)
                .title("Use your Quotes")
                .description("Add Your Quotes to Favorites from Favorites Screen")
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

    private void myAlarm(Calendar calendar) {

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }
}