package phone.vishnu.quotes.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.fragment.TourSingleFragment;
import phone.vishnu.quotes.model.TourItem;

public class TourFragmentStateAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    private ArrayList<TourItem> tourItems = new ArrayList<>();

    public TourFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.tourItems = getTourItems();
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

    private ArrayList<TourItem> getTourItems() {

        ArrayList<TourItem> tourItems = new ArrayList<>();

        tourItems.add(new TourItem(R.drawable.ic_quotes, "Spread positivity with us", "Would you try?"));
        tourItems.add(new TourItem(R.drawable.ic_quotes_round, "It Works Like...", "Share Quotations as an Image without any hassles of image editing..."));
        tourItems.add(new TourItem(R.drawable.ic_unfold_more, "Scroll For More", "Scroll horizontally to get more Quotes"));
        tourItems.add(new TourItem(R.drawable.ic_insert_photo, "Background Image", "Customize Background Image from the Overflow Menu"));
        tourItems.add(new TourItem(R.drawable.ic_color_lens, "Accent Color", "Customize Accent Colour from the Overflow Menu"));
        tourItems.add(new TourItem(R.drawable.ic_font, "Custom Font", "Customize Font from the Overflow Menu"));
        tourItems.add(new TourItem(R.drawable.ic_search, "Search", "Search for Quotes"));
        tourItems.add(new TourItem(R.drawable.ic_notifications, "Daily Notification of Quotes", "Daily Notifications with Quotes. You can customize the time later"));
        tourItems.add(new TourItem(R.drawable.ic_share, "Share Quotes in Social Media", "Share Quote as an HD Image"));
        tourItems.add(new TourItem(R.drawable.ic_favorite, "Favorite Quotes", "Add to Favourites for easy-access of Quotes"));
        tourItems.add(new TourItem(R.drawable.ic_post_add, "Use your Quotes", "Add Custom Quotes Favorites Screen"));
        tourItems.add(new TourItem(R.drawable.ic_whatshot, "That's it", "Get Started"));

        return tourItems;
    }
}
