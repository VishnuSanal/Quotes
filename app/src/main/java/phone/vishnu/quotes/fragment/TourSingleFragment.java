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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.Constants;
import phone.vishnu.quotes.model.TourItem;

public class TourSingleFragment extends Fragment {

    public TourSingleFragment() {}

    public static TourSingleFragment newInstance(TourItem tourItem) {
        Bundle args = new Bundle();

        args.putInt(Constants.TOUR_IMG_EXTRA, tourItem.getImgId());
        args.putString(Constants.TOUR_TITLE_EXTRA, tourItem.getTitle());
        args.putString(Constants.TOUR_DESCRIPTION_EXTRA, tourItem.getDesc());

        TourSingleFragment fragment = new TourSingleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View i = inflater.inflate(R.layout.tour_single_item, container, false);

        Bundle args = getArguments();

        if (args != null) {
            ((ImageView) i.findViewById(R.id.tourImage))
                    .setImageResource(args.getInt(Constants.TOUR_IMG_EXTRA));
            ((TextView) i.findViewById(R.id.tourTitle))
                    .setText(args.getString(Constants.TOUR_TITLE_EXTRA));
            ((TextView) i.findViewById(R.id.tourDescription))
                    .setText(args.getString(Constants.TOUR_DESCRIPTION_EXTRA));
        }

        return i;
    }
}
