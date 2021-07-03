package phone.vishnu.quotes.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.karumi.dexter.Dexter;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.SplashActivity;
import phone.vishnu.quotes.adapter.TourViewPagerAdapter;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class TourFragment extends Fragment {

    private ViewPager2 viewPager;

    private TourViewPagerAdapter adapter;
    private Button nextButton, skipButton;

    private SharedPreferenceHelper sharedPreferenceHelper;

    public TourFragment() {
    }

    public static TourFragment newInstance() {
        return new TourFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_tour, container, false);

        viewPager = inflate.findViewById(R.id.splashScreenTourViewPager);
        TabLayout tabLayout = inflate.findViewById(R.id.splashScreenTourTabLayout);

        nextButton = inflate.findViewById(R.id.splashScreenNextButton);
        skipButton = inflate.findViewById(R.id.splashScreenSkipButton);

        adapter = new TourViewPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, true, (tab, position) -> {

        }).attach();

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final int pageCount = adapter.getItemCount() - 1;


        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pageCount)
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            else {
                tourCompleted();
            }
        });

        skipButton.setOnClickListener(v -> tourCompleted());

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if ((position == pageCount))
                    tourCompleted();
            }
        });
    }

    private void tourCompleted() {
        sharedPreferenceHelper.setNewFirstRunBoolean(false);

        Dexter.withContext(requireContext())
                .withPermissions(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE);

        ((SplashActivity) requireActivity()).moveToNext();

    }
}
