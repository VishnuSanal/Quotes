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

package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.karumi.dexter.Dexter;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.adapter.TourViewPagerAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class TourFragment extends Fragment {

    private ViewPager2 viewPager;

    private TourViewPagerAdapter adapter;
    private Button nextButton, skipButton;

    private SharedPreferenceHelper sharedPreferenceHelper;

    public TourFragment() {}

    public static TourFragment newInstance() {
        return new TourFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_tour, container, false);

        viewPager = inflate.findViewById(R.id.splashScreenTourViewPager);
        TabLayout tabLayout = inflate.findViewById(R.id.splashScreenTourTabLayout);

        nextButton = inflate.findViewById(R.id.splashScreenNextButton);
        skipButton = inflate.findViewById(R.id.splashScreenSkipButton);

        adapter = new TourViewPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {}).attach();

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int pageCount = adapter.getItemCount() - 1;

        nextButton.setOnClickListener(
                v -> {
                    if (viewPager.getCurrentItem() < pageCount)
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    else {
                        tourCompleted();
                    }
                });

        skipButton.setOnClickListener(v -> tourCompleted());

        viewPager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageScrolled(
                            int position, float positionOffset, int positionOffsetPixels) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                        if ((position == pageCount)) tourCompleted();
                    }
                });
    }

    private void tourCompleted() {
        sharedPreferenceHelper.setNewFirstRunBoolean(false);
        sharedPreferenceHelper.setFirstRunBoolean(false);

        Dexter.withContext(requireContext())
                .withPermissions(
                        Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

        ((SplashActivity) requireActivity()).moveToNext();
    }
}
