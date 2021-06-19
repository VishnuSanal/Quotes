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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import phone.vishnu.quotes.BuildConfig;
import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.AboutAdapter;
import phone.vishnu.quotes.model.TourItem;

public class AboutFragment extends Fragment {

    private RecyclerView recyclerView;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_about, container, false);
        ((TextView) inflate.findViewById(R.id.aboutSampleVersion)).setText(String.format("Version %s", BuildConfig.VERSION_NAME));

        recyclerView = inflate.findViewById(R.id.aboutRecyclerView);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AboutAdapter adapter = new AboutAdapter();
        adapter.submitList(getItems());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            if (position == 0) {
                openLink("https://t.me/QuotesStatusCreator");
            } else if (position == 1) {
                openLink("https://instagram.com/quotes_status_creator");
            } else if (position == 2) {
                openLink("https://t.me/DailyQuotesStatus");
            } else if (position == 3) {

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

            } else if (position == 4) {
                openLink("https://github.com/VishnuSanal/Quotes");
            } else if (position == 5) {
                openLink("https://github.com/VishnuSanal/Quotes/blob/master/THANKS.md");
            } else if (position == 6) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{requireContext().getString(R.string.email_address_of_developer)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback of " + requireContext().getString(R.string.app_name));

                if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void openLink(String s) {
        Uri uriUrl = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uriUrl));
    }

    private List<TourItem> getItems() {
        return Arrays.asList(
                new TourItem(R.drawable.ic_home, "Join Telegram Group", "Join our community on Telegram"),
                new TourItem(R.drawable.ic_whatshot, "Follow on Instagram", "Follow Quotes Status Creator on Instagram"),
                new TourItem(R.drawable.ic_color_lens, "Join Telegram Channel", "Get a daily dose of inspiration on your inbox!"),
                new TourItem(R.drawable.ic_done, "Rate the App", "Rate this app on Google Play"),
                new TourItem(R.drawable.ic_info, "Source Code", "View source code on GitHub"),
                new TourItem(R.drawable.ic_favorite, "Thanks To", "Thanks to these awesome people"),
                new TourItem(R.drawable.ic_settings, " Send Feedback", " Send feedback to developer")
        );
    }
}
