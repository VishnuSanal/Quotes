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

package phone.vishnu.quotes.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.ArrayList;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.fragment.TourSingleFragment;
import phone.vishnu.quotes.model.TourItem;

public class TourViewPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<TourItem> tourItems;

    public TourViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.tourItems = getTourItems(fragmentActivity);
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

    private ArrayList<TourItem> getTourItems(FragmentActivity fragmentActivity) {

        ArrayList<TourItem> tourItems = new ArrayList<>();

        tourItems.add(
                new TourItem(
                        R.drawable.ic_tour_announcement,
                        fragmentActivity.getString(R.string.welcome),
                        fragmentActivity.getString(R.string.welcome_desc)));

        tourItems.add(
                new TourItem(
                        R.drawable.ic_tour_go,
                        fragmentActivity.getString(R.string.highly_customizable),
                        fragmentActivity.getString(R.string.highly_customizable_desc)));

        tourItems.add(
                new TourItem(
                        R.drawable.ic_tour_app,
                        fragmentActivity.getString(R.string.feature_rich),
                        fragmentActivity.getString(R.string.huge_collection_desc)));

        return tourItems;
    }
}
