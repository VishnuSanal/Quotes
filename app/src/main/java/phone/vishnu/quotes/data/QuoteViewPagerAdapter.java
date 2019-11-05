package phone.vishnu.quotes.data;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class QuoteViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public QuoteViewPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        fragments = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
