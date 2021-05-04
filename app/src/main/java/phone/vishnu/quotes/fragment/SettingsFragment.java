package phone.vishnu.quotes.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
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

import com.ncorti.slidetoact.SlideToActView;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.receiver.NotificationReceiver;

import static android.content.Context.ALARM_SERVICE;

public class SettingsFragment extends Fragment {

    private SwitchCompat reminderSwitch;
    private SlideToActView resetToggle;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private TextView shareActionPickTV;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(Objects.requireNonNull(requireContext()));

        resetToggle = inflate.findViewById(R.id.settingsResetToggle);

        reminderSwitch = inflate.findViewById(R.id.settingsReminderSwitch);

        shareActionPickTV = inflate.findViewById(R.id.settingsShareActionPickTV);

        reminderSwitch.setText(getSwitchText(sharedPreferenceHelper.getAlarmString()));
        shareActionPickTV.setText(getSpannableText("Share", "What share button does"));

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetToggle.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {

                resetSettings(requireContext());

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

        shareActionPickTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFragment bottomSheet = BottomSheetFragment.newInstance();
                bottomSheet.show(requireActivity().getSupportFragmentManager(), "ModalBottomSheet");
            }
        });
    }

    private void resetSettings(Context c) {

        sharedPreferenceHelper.resetSharedPreferences();

        deleteFiles(c);
    }

    private void deleteFiles(Context c) {
        ExportHelper exportHelper = new ExportHelper(c);

        File BGFile = new File(exportHelper.getBGPath());
        File SSFile = new File(exportHelper.getSSPath());

        if (BGFile.exists())
            BGFile.delete();
        if (SSFile.exists())
            SSFile.delete();

        requireActivity().finish();

        if (getContext() != null)
            requireActivity().startActivity(
                    new Intent(requireContext(), SplashActivity.class
                    ));

        Toast.makeText(requireContext(), "Settings Reset\nRestarting App for changes to take effect", Toast.LENGTH_SHORT).show();
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
        return getSpannableText("Daily Reminder", v);
    }

    private SpannableString getSpannableText(String s1, String s2) {

        String s = s1 + "\n" + s2;

        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, s1.length(), 0);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, s1.length(), 0);

        return spannableString;

    }
}