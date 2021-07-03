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

        if (!new SharedPreferenceHelper(context).isFirstRun() && new SharedPreferenceHelper(context).isNewFirstRun()) {

            tourItems.add(new TourItem(R.drawable.ic_tour_announcement, "We're v2.0.0!",
                    "Quotes Status Creator got updated to v2.0.0!\nEnjoy the new and updated app!"));
        } else

            tourItems.add(new TourItem(R.drawable.ic_tour_announcement, "Welcome!", "Welcome to Quotes Status Creator"));

        tourItems.add(new TourItem(R.drawable.ic_tour_app, "Highly Customizable",
                "Quotes Status Creator lets you share quotations as status images on social media"));

        tourItems.add(new TourItem(R.drawable.ic_tour_go, "Spread positivity with us", "Get started by sharing a Status!"));

        return tourItems;
    }
}
