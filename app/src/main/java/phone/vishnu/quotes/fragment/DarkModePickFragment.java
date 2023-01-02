/*
 * Copyright (C) 2019 - 2023 Vishnu Sanal. T
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

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class DarkModePickFragment extends BaseBottomSheetDialogFragment {

    private RadioGroup radioGroup;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public DarkModePickFragment() {}

    public static DarkModePickFragment newInstance() {
        return new DarkModePickFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_dark_mode_pick, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        radioGroup = inflate.findViewById(R.id.darkModePickRadioGroup);

        setChecked(sharedPreferenceHelper.getAppThemePreference());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener(
                (group, id) -> {
                    optionChecked(getInt(id));

                    dismiss();
                });
    }

    private void optionChecked(int i) {

        sharedPreferenceHelper.setAppThemePreference(i);

        if (i == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (i == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (i == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void setChecked(int i) {
        if (i == 0) {
            ((RadioButton) radioGroup.findViewById(R.id.darkModePickLightRadioButton))
                    .setChecked(true);
        } else if (i == 1) {
            ((RadioButton) radioGroup.findViewById(R.id.darkModePickDarkRadioButton))
                    .setChecked(true);
        } else if (i == 2) {
            ((RadioButton) radioGroup.findViewById(R.id.darkModePickSystemRadioButton))
                    .setChecked(true);
        }
    }

    private int getInt(int id) {
        // Light -> 0
        // Dark -> 1
        // System -> 2

        if (id == R.id.darkModePickLightRadioButton) return 0;
        else if (id == R.id.darkModePickDarkRadioButton) return 1;
        else if (id == R.id.darkModePickSystemRadioButton) return 2;
        return 2;
    }

    private boolean isNightMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }
}
