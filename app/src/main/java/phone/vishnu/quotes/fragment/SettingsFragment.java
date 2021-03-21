package phone.vishnu.quotes.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.receiver.NotificationReceiver;

import static android.content.Context.ALARM_SERVICE;

public class SettingsFragment extends Fragment {

    private SwitchCompat reminderSwitch;
    private TextView resetTV;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_settings, container, false);
        resetTV = inflate.findViewById(R.id.settingsResetButton);
        reminderSwitch = inflate.findViewById(R.id.settingsReminderSwitch);

        sharedPreferenceHelper = new SharedPreferenceHelper(Objects.requireNonNull(requireContext()));

        reminderSwitch.setText(getSwitchText(sharedPreferenceHelper.getAlarmString()));

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sharedPreferenceHelper.resetSharedPreferences();

                Toast.makeText(requireContext(), "Settings Reset.....\nRestart App for changes to take effect.....", Toast.LENGTH_SHORT).show();
            }
        });

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    final Calendar c = Calendar.getInstance();

                    TimePickerDialog timePicker = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);

                            sharedPreferenceHelper.setAlarmString("At " + hourOfDay + " : " + minute + " Daily");

                            reminderSwitch.setText(getSwitchText(MessageFormat.format("At {0} : {1} Daily", hourOfDay, minute)));

                            myAlarm(c);

                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(requireContext()));
                    timePicker.show();

                } else {
                    reminderSwitch.setText(getSwitchText(""));

                    sharedPreferenceHelper.setAlarmString("Alarm Not Set");

                    Intent intent = new Intent(requireContext(), NotificationReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(ALARM_SERVICE);

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                    }
                }
            }
        });

    }

    private void myAlarm(Calendar calendar) {

        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(requireContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) requireContext().getApplicationContext().getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }

    }

    private SpannableString getSwitchText(String v) {
        String s = "Daily Reminder" + "\n" + v;

        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 14, 0);

        return spannableString;
    }
}