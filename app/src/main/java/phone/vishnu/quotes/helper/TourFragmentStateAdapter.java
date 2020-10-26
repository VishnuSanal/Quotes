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
        tourItems.add(new TourItem(R.drawable.ic_quotes_round, "It Works Like........", "Share Quotations from World Leaders as an Image that too without any hassles of image editing......"));
        tourItems.add(new TourItem(R.drawable.ic_unfold_more, "Scroll For More", "Scroll horizontally to get more awesome Quotes"));
        tourItems.add(new TourItem(R.drawable.ic_insert_photo, "Background Image", "You can select a custom background image for the app from the overflow menu on the bottom of the screen"));
        tourItems.add(new TourItem(R.drawable.ic_color_lens, "Custom Accent Color", "You can select a custom accent colour for the app from the overflow menu on the bottom of the screen"));
        tourItems.add(new TourItem(R.drawable.ic_font, "Custom Font", "You can select a custom font for the app from the overflow menu on the bottom of the screen"));
        tourItems.add(new TourItem(R.drawable.ic_notifications, "Daily Notification of Quotes", "You will receive daily notifications with Quotes at 08:30 AM. You can change the time here. You can customise this later from the about screen"));
        tourItems.add(new TourItem(R.drawable.ic_share, "Share Quotes in Social Media", "Click share icon from a Quote and select to share that Quote in the form of an Image"));
        tourItems.add(new TourItem(R.drawable.ic_favorite, "Add a Quote to Favorites", "Click favourite icon from a quote. You can view the favorite quotes by clicking on the overflow menu on the bottom of the screen"));
        tourItems.add(new TourItem(R.drawable.ic_post_add, "Use your Quotes", "Add Your Quotes to Favorites from Favorites Screen"));
        tourItems.add(new TourItem(R.drawable.ic_whatshot, "That's it", "Get Started"));

        return tourItems;
    }
}
