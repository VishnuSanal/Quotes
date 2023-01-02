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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;
import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.adapter.AboutRVAdapter;
import phone.vishnu.quotes.model.TourItem;

public class AboutFragment extends BaseBottomSheetDialogFragment {

    private RecyclerView recyclerView;

    public AboutFragment() {}

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_about, container, false);
        ((TextView) inflate.findViewById(R.id.aboutSampleVersion))
                .setText(
                        String.format(
                                "%s %s", getString(R.string.version), BuildConfig.VERSION_NAME));

        recyclerView = inflate.findViewById(R.id.aboutRecyclerView);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AboutRVAdapter adapter = new AboutRVAdapter();
        adapter.submitList(getItems());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(
                position -> {
                    if (position == 0) {
                        openLink("https://t.me/QuotesStatusCreator");
                    } else if (position == 1) {
                        openLink("https://instagram.com/quotes_status_creator");
                    } else if (position == 2) {
                        openLink("https://t.me/DailyQuotesStatus");
                    } else if (position == 3) {

                        Uri uriUrl =
                                Uri.parse(
                                        "market://details?id=" + requireContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NO_HISTORY
                                        | Intent.FLAG_ACTIVITY_NEW_DOCUMENT
                                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            startActivity(
                                    new Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse(
                                                    "http://play.google.com/store/apps/details?id="
                                                            + requireContext().getPackageName())));
                        }

                    } else if (position == 4) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(
                                Intent.EXTRA_TEXT,
                                String.format(
                                        "%s\n\n%s",
                                        getString(R.string.share_text),
                                        "https://play.google.com/store/apps/details?id=phone.vishnu.quotes"));
                        Intent chooser =
                                Intent.createChooser(
                                        intent, getString(R.string.share_quotes_status_creator));
                        startActivity(chooser);
                    } else if (position == 5) {
                        openLink("https://hosted.weblate.org/engage/quotes-status-creator/");
                    } else if (position == 6) {
                        openLink("https://github.com/VishnuSanal/Quotes");
                    } else if (position == 7) {
                        openLink("https://github.com/VishnuSanal/Quotes/blob/master/THANKS.md");
                    }
                });
    }

    private void openLink(String s) {
        Uri uriUrl = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
    }

    private List<TourItem> getItems() {
        return Arrays.asList(
                new TourItem(
                        R.drawable.ic_home,
                        getString(R.string.join_telegram_group),
                        getString(R.string.join_our_community_on_telegram)),
                new TourItem(
                        R.drawable.ic_whatshot,
                        getString(R.string.follow_on_instagram),
                        getString(R.string.follow_quotes_status_creator_on_instagram)),
                new TourItem(
                        R.drawable.ic_color_lens,
                        getString(R.string.join_telegram_channel),
                        getString(R.string.get_a_daily_dose_of_inspiration_on_your_inbox)),
                new TourItem(
                        R.drawable.ic_done,
                        getString(R.string.rate_the_app),
                        getString(R.string.rate_this_app_on_google_play)),
                new TourItem(
                        R.drawable.ic_share,
                        getString(R.string.share_the_app),
                        getString(R.string.share_quotes_status_creator_with_your_friends)),
                new TourItem(
                        R.drawable.ic_translate,
                        getString(R.string.translate_the_app),
                        getString(R.string.help_in_localization)),
                new TourItem(
                        R.drawable.ic_info,
                        getString(R.string.source_code),
                        getString(R.string.view_source_code_on_github)),
                new TourItem(
                        R.drawable.ic_favorite,
                        getString(R.string.thanks_to),
                        getString(R.string.thanks_to_these_awesome_people)));
    }
}
