package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import phone.vishnu.quotes.R;

public class FontMasterFragment extends Fragment {

    private ImageView fontSelector, colorSelector;

    public FontMasterFragment() {
    }

    public static FontMasterFragment newInstance() {
        return new FontMasterFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_font_master, container, false);

        colorSelector = inflate.findViewById(R.id.fontColorSampleIconImageView);
        fontSelector = inflate.findViewById(R.id.fontFontSampleIconImageView);

        requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.masterFragmentHost, FontFragment.newInstance()).commit();

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fontSelector.setOnClickListener(v -> {
            fontSelector.setElevation(DP2PX(12));
            colorSelector.setElevation(DP2PX(8));

            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.masterFragmentHost, FontFragment.newInstance()).commit();
        });
        colorSelector.setOnClickListener(v -> {
            colorSelector.setElevation(DP2PX(12));
            fontSelector.setElevation(DP2PX(8));

            //TODO:
            ColorPickFragment.newInstance(2)
                    .show(
                            requireActivity().getSupportFragmentManager(),
                            "ColorPickBottomSheetDialogFragment"
                    );

        });
    }

    private int DP2PX(float DP) {
        final float scale = requireContext().getResources().getDisplayMetrics().density;
        return (int) (DP * scale + 0.5f);
    }
}