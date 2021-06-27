package phone.vishnu.quotes.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;

import static phone.vishnu.quotes.activity.MainActivity.bgDialog;

public class BackgroundOptionPickFragment extends BottomSheetDialogFragment {

    private static final String CANCELLABLE_EXTRA = "isCancellable";

    private RadioGroup radioGroup;

    public static BackgroundOptionPickFragment newInstance(boolean isCancellable) {
        BackgroundOptionPickFragment bottomSheetFragment = new BackgroundOptionPickFragment();

        Bundle bundle = new Bundle();

        bundle.putBoolean(CANCELLABLE_EXTRA, isCancellable);

        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

        if (getArguments() != null)
            if (getArguments().containsKey(CANCELLABLE_EXTRA))
                setCancelable(getArguments().getBoolean(CANCELLABLE_EXTRA));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.background_option_pick_dialog, container, false);

        radioGroup = inflate.findViewById(R.id.backgroundPickRadioGroup);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener((group, id) -> {

            if (id == R.id.backgroundPickColorRadioButton) {
                ColorPickFragment.newInstance(0)
                        .show(
                                requireActivity().getSupportFragmentManager(),
                                "ColorPickFragment"
                        );

                dismiss();

            } else if (id == R.id.backgroundPickGalleryRadioButton) {

                requireActivity().startActivityForResult(
                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"),
                        MainActivity.PICK_IMAGE_ID
                );

                dismiss();

            } else if (id == R.id.backgroundPickDefaultRadioButton) {

                bgDialog = new ProgressDialog(requireContext(), R.style.DialogTheme);
                bgDialog.setMessage("Please Wait....");
                bgDialog.show();
                bgDialog.setCancelable(false);

                PickFragment.newInstance()
                        .show(
                                requireActivity().getSupportFragmentManager(),
                                "PickFragment"
                        );

                dismiss();
            }
        });
    }
}