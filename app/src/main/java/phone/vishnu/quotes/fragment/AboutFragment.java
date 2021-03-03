package phone.vishnu.quotes.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
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

import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.receiver.NotificationReceiver;

import static android.content.Context.ALARM_SERVICE;

public class AboutFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;
    private TextView sourceCodeTV, feedbackTV, resetTV, reminderTimeTV, thanksTV, rateTV;
    private SwitchCompat reminderSwitch;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_about, container, false);
        sourceCodeTV = inflate.findViewById(R.id.aboutPageViewSourceCodeTextView);
        feedbackTV = inflate.findViewById(R.id.aboutPageFeedbackTextView);
        resetTV = inflate.findViewById(R.id.aboutResetSettingsButton);
        reminderSwitch = inflate.findViewById(R.id.aboutReminderSwitch);
        reminderTimeTV = inflate.findViewById(R.id.aboutReminderTV);
        thanksTV = inflate.findViewById(R.id.aboutPageThanksTextView);
        rateTV = inflate.findViewById(R.id.aboutPageRateTextView);
        ((TextView) inflate.findViewById(R.id.aboutSampleVersion)).setText(String.format("Version %s", BuildConfig.VERSION_NAME));

        sharedPreferenceHelper = new SharedPreferenceHelper(Objects.requireNonNull(requireContext()));

        reminderTimeTV.setText(sharedPreferenceHelper.getAlarmString());
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

                composeEmail(new String[]{requireContext().getString(R.string.email_address_of_developer)}, "Feedback of " + requireContext().getString(R.string.app_name));

            }
        });
        thanksTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://github.com/VishnuSanal/Quotes/blob/master/THANKS.md");
                startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
            }
        });
        rateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uriUrl = Uri.parse("market://details?id=" + requireContext().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    startActivity(
                            new Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().getPackageName())));
                }
            }
        });
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

                            reminderTimeTV.setText(MessageFormat.format("At {0} : {1} Daily", hourOfDay, minute));
                            reminderTimeTV.setVisibility(View.VISIBLE);

                            myAlarm(c);

                        }
                    }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(requireContext()));
                    timePicker.show();

                } else {
                    reminderTimeTV.setVisibility(View.GONE);

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

    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
