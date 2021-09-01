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

package phone.vishnu.quotes.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.fragment.TourSingleFragment;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;
import phone.vishnu.quotes.model.TourItem;

public class TourViewPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<TourItem> tourItems;

    public TourViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.tourItems = getTourItems(fragmentActivity.getApplicationContext());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return TourSingleFragment.newInstance(getTourItem(position));
    }

    @Override
    public int getItemCount() {
        return tourItems.size();
    }

    private TourItem getTourItem(int position) {
        return tourItems.get(position);
    }

    private ArrayList<TourItem> getTourItems(Context context) {

        ArrayList<TourItem> tourItems = new ArrayList<>();

        if (!new SharedPreferenceHelper(context).isFirstRun()
                && new SharedPreferenceHelper(context).isNewFirstRun())
            tourItems.add(
                    new TourItem(
                            R.drawable.ic_tour_announcement,
                            "We're v2.0.0!",
                            "Quotes Status Creator got updated to v2.0.0!\nEnjoy the new and updated app!"));
        else
            tourItems.add(
                    new TourItem(
                            R.drawable.ic_tour_announcement,
                            "Welcome!",
                            "Welcome to Quotes Status Creator"));

        tourItems.add(
                new TourItem(
                        R.drawable.ic_tour_app,
                        "It works Like",
                        "Quotes Status Creator lets you share quotations as status images on social media"));

        tourItems.add(
                new TourItem(
                        R.drawable.ic_tour_go,
                        "Spread positivity with us",
                        "Get started by sharing a Status!"));

        return tourItems;
    }
}
