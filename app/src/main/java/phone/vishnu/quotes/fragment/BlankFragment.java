package phone.vishnu.quotes.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.receiver.NotificationReceiver;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class BlankFragment extends Fragment {
    private TextView sourceCodeTV, feedbackTV, resetTV, reminderTimeTV;
    private Switch reminderSwitch;

    public BlankFragment() {
    }

    public static BlankFragment newInstance() {
        return new BlankFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_blank, container, false);
        sourceCodeTV = inflate.findViewById(R.id.aboutPageViewSourceCodeTextView);
        feedbackTV = inflate.findViewById(R.id.aboutPageFeedbackTextView);
        resetTV = inflate.findViewById(R.id.aboutResetSettingsButton);
        reminderSwitch = inflate.findViewById(R.id.aboutReminderSwitch);
        reminderTimeTV = inflate.findViewById(R.id.aboutReminderTV);
        String ALARM_PREFERENCE_TIME = "customAlarmPreference";
        reminderTimeTV.setText(getActivity().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE)
                .getString(ALARM_PREFERENCE_TIME, "At 08:30 Daily"));
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceCodeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://github.com/VishnuSanal/Quotes");
                startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
            }
        });
        feedbackTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                composeEmail(new String[]{getActivity().getString(R.string.email_address_of_developer)}, "Feedback of " + getActivity().getString(R.string.app_name));

            }
        });
        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String COLOR_PREFERENCE_NAME = "colorPreference";
                String BACKGROUND_PREFERENCE_NAME = "backgroundPreference";
                String FIRST_RUN_BOOLEAN = "firstRunPreference";
//                String IS_CUSTOM_ALARM_SET = "customAlarmPreferenceBoolean";
                String ALARM_PREFERENCE_TIME = "customAlarmPreference";

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).edit();

                editor.putString(COLOR_PREFERENCE_NAME, "#5C5C5C");
                editor.putString(BACKGROUND_PREFERENCE_NAME, "-1");
                editor.putString(ALARM_PREFERENCE_TIME, "At 08:30 Daily");
                editor.putBoolean(FIRST_RUN_BOOLEAN, true);

                editor.apply();

                Toast.makeText(getActivity(), "Settings Reset.....\nRestart App for changes to take effect.....", Toast.LENGTH_SHORT).show();
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                final String ALARM_PREFERENCE_TIME = "customAlarmPreference";
                final SharedPreferences.Editor preferences = getActivity().getSharedPreferences("phone.vishnu.quotes.sharedPreferences", MODE_PRIVATE).edit();

                if (isChecked) {

                    final Calendar c = Calendar.getInstance();

                    TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);

                            preferences.putString(ALARM_PREFERENCE_TIME, "At " + hourOfDay + " : " + minute + " Daily").apply();

                            reminderTimeTV.setText(MessageFormat.format("At {0} : {1} Daily", hourOfDay, minute));
                            reminderTimeTV.setVisibility(View.VISIBLE);

                            myAlarm(c);

                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(getActivity()));
                    timePicker.show();

                } else {
                    reminderTimeTV.setVisibility(View.GONE);

                    preferences.putString(ALARM_PREFERENCE_TIME, "Alarm Not Set").apply();

                    Intent intent = new Intent(getActivity(), NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        });

    }

    private void myAlarm(Calendar calendar) {

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    private void composeEmail(String[] addresses, String subject) {
/*
        File outputFile = new File(Environment.getExternalStorageDirectory(), "logcat.txt");
        try {
            Runtime.getRuntime().exec("logcat -f " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//        intent.putExtra(Intent.EXTRA_STREAM, outputFile.getAbsolutePath());
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
