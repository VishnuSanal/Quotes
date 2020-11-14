package phone.vishnu.quotes.helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import phone.vishnu.quotes.fragment.QuoteFragment;
import phone.vishnu.quotes.model.Quote;

public class QuoteViewPagerAdapter extends FragmentPagerAdapter {
    private List<Quote> quoteList;

    public QuoteViewPagerAdapter(FragmentManager fm, List<Quote> fragmentList) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        quoteList = fragmentList;
    }

    @androidx.annotation.NonNull
    @Override
    public Fragment getItem(int position) {
        return QuoteFragment.newInstance(quoteList.get(position));
    }

    @Override
    public int getCount() {
        return quoteList.size();
    }

    @Override
    public int getItemPosition(@androidx.annotation.NonNull Object object) {
        return POSITION_NONE;
    }

    public void setQuoteList(List<Quote> quoteList) {
        this.quoteList = quoteList;
    }
}