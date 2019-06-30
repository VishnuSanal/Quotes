package phone.vishnu.quotes.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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
