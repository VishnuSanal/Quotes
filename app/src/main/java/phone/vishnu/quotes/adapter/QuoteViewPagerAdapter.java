package phone.vishnu.quotes.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

import phone.vishnu.quotes.fragment.QuoteFragment;
import phone.vishnu.quotes.model.Quote;

public class QuoteViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Quote> quoteList;

    public QuoteViewPagerAdapter(FragmentManager fragmentManager, List<Quote> quoteList) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.quoteList = quoteList;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return QuoteFragment.newInstance(quoteList.get(position));
    }

    @Override
    public int getCount() {
        return quoteList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    public void setQuoteList(List<Quote> quoteList) {
        this.quoteList = quoteList;
    }
}