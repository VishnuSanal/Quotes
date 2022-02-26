/*
 * Copyright (C) 2019 - 2022 Vishnu Sanal. T
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

package phone.vishnu.quotes.fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import com.ncorti.slidetoact.SlideToActView;
import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Objects;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.helper.AlarmHelper;
import phone.vishnu.quotes.helper.ExportHelper;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class SettingsFragment extends BaseBottomSheetDialogFragment {

    private SwitchCompat reminderSwitch, favActionSwitch;
    private SlideToActView resetToggle;
    private SharedPreferenceHelper sharedPreferenceHelper;
    private TextView shareActionPickTV, darkModePickTV;

    public SettingsFragment() {}

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_settings, container, false);

        sharedPreferenceHelper =
                new SharedPreferenceHelper(Objects.requireNonNull(requireContext()));

        resetToggle = inflate.findViewById(R.id.settingsResetToggle);

        reminderSwitch = inflate.findViewById(R.id.settingsReminderSwitch);
        favActionSwitch = inflate.findViewById(R.id.settingsFavReverseSwitch);

        shareActionPickTV = inflate.findViewById(R.id.settingsShareActionPickTV);
        darkModePickTV = inflate.findViewById(R.id.settingsDarkModePickTV);

        reminderSwitch.setChecked(!sharedPreferenceHelper.getAlarmString().equals("Alarm Not Set"));
        favActionSwitch.setChecked(sharedPreferenceHelper.isFavActionReversed());

        reminderSwitch.setText(getSwitchText(sharedPreferenceHelper.getAlarmString()));
        favActionSwitch.setText(
                getSpannableText(
                        getString(R.string.reverse_swipe),
                        getString(R.string.reverse_swipe_action_on_favorites)));
        shareActionPickTV.setText(
                getSpannableText(
                        getString(R.string.share), getString(R.string.share_button_action)));
        darkModePickTV.setText(
                getSpannableText(
                        getString(R.string.theme), getString(R.string.pick_a_theme_for_the_app)));

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resetToggle.setOnSlideCompleteListener(slideToActView -> resetSettings(requireContext()));

        reminderSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {

                        Calendar c = Calendar.getInstance();

                        // TODO: Find a way to implement this in Bottom Sheet!

                        TimePickerDialog timePickerDialog =
                                new TimePickerDialog(
                                        requireContext(),
                                        getPickerTheme(requireContext()),
                                        (view1, hourOfDay, minute) -> {
                                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                            c.set(Calendar.MINUTE, minute);

                                            sharedPreferenceHelper.setAlarmString(
                                                    "At " + hourOfDay + " : " + minute + " Daily");

                                            reminderSwitch.setText(
                                                    getSwitchText(
                                                            MessageFormat.format(
                                                                    "At {0} : {1} Daily",
                                                                    hourOfDay, minute)));

                                            AlarmHelper.setAlarm(requireContext(), c);
                                        },
                                        c.get(Calendar.HOUR_OF_DAY),
                                        c.get(Calendar.MINUTE),
                                        DateFormat.is24HourFormat(requireContext()));

                        timePickerDialog.setOnCancelListener(d -> reminderSwitch.setChecked(false));

                        timePickerDialog.show();

                    } else {
                        alarmTurnedOff(requireContext());
                    }
                });

        favActionSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    sharedPreferenceHelper.setFavActionReverse(isChecked);
                });

        shareActionPickTV.setOnClickListener(
                v -> {
                    ShareOptionPickFragment bottomSheet = ShareOptionPickFragment.newInstance();
                    bottomSheet.show(
                            requireActivity().getSupportFragmentManager(), "SettingsFragment");

                    dismiss();
                });

        darkModePickTV.setOnClickListener(
                v -> {
                    DarkModePickFragment bottomSheet = DarkModePickFragment.newInstance();
                    bottomSheet.show(
                            requireActivity().getSupportFragmentManager(), "SettingsFragment");

                    dismiss();
                });
    }

    private void alarmTurnedOff(Context context) {
        reminderSwitch.setText(getSwitchText(""));

        sharedPreferenceHelper.setAlarmString("Alarm Not Set");

        AlarmHelper.cancelAlarm(context);
    }

    private void resetSettings(Context c) {

        sharedPreferenceHelper.resetSharedPreferences();

        deleteFiles(c);
    }

    private void deleteFiles(Context c) {
        ExportHelper exportHelper = new ExportHelper(c);

        File BGFile = new File(exportHelper.getBGPath());
        File SSFile = new File(exportHelper.getSSPath());

        if (BGFile.exists()) BGFile.delete();
        if (SSFile.exists()) SSFile.delete();

        requireActivity().finish();

        if (getContext() != null)
            requireActivity().startActivity(new Intent(requireContext(), SplashActivity.class));

        Toast.makeText(
                        requireContext(),
                        String.format(
                                "%s\n%s",
                                getString(R.string.settings_reset),
                                getString(R.string.restarting_app_for_changes_to_take_effect)),
                        Toast.LENGTH_SHORT)
                .show();
    }

    private SpannableString getSwitchText(String v) {
        return getSpannableText(getString(R.string.daily_reminder), v);
    }

    private SpannableString getSpannableText(String s1, String s2) {

        String s = s1 + "\n" + s2;

        SpannableString spannableString = new SpannableString(s);
        spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, s1.length(), 0);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.textColor)),
                0,
                s1.length(),
                0);
        spannableString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.textColorLight)),
                s1.length(),
                s.length(),
                0);

        return spannableString;
    }

    private int getPickerTheme(Context context) {
        return (sharedPreferenceHelper.getAppThemePreference() == 1
                        || (sharedPreferenceHelper.getAppThemePreference() == 2
                                && (context.getResources().getConfiguration().uiMode
                                                & Configuration.UI_MODE_NIGHT_MASK)
                                        == Configuration.UI_MODE_NIGHT_YES))
                ? AlertDialog.THEME_DEVICE_DEFAULT_DARK
                : AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
    }
}
