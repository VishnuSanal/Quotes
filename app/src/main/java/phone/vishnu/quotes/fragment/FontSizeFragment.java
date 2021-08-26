package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.Slider;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class FontSizeFragment extends BottomSheetDialogFragment {

    private Slider sizeSlider;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public FontSizeFragment() {
    }

    public static FontSizeFragment newInstance() {
        return new FontSizeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font_size, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        sizeSlider = inflate.findViewById(R.id.fontSizeSlider);
        sizeSlider.setLabelFormatter(value -> (int) value + "sp");
        sizeSlider.setValue(sharedPreferenceHelper.getFontSizePreference());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sizeSlider.addOnChangeListener((slider, value, fromUser) -> {

            if (fromUser) {
                sharedPreferenceHelper.setFontSizePreference(value);

                ((MainActivity) requireActivity()).updateViewPager();
            }

        });

    }
}