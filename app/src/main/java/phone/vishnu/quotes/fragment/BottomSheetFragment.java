package phone.vishnu.quotes.fragment;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private RadioGroup radioGroup;
    private SharedPreferenceHelper sharedPreferenceHelper;

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.bottom_sheet, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(requireContext());

        radioGroup = inflate.findViewById(R.id.bottomSheetRadioGroup);

        setChecked(sharedPreferenceHelper.getShareButtonAction());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup.setOnCheckedChangeListener((group, id) -> {

            sharedPreferenceHelper.setShareButtonAction(getInt(id));

            ((MainActivity) requireActivity()).updateViewPager();

            ((ImageView) view.findViewById(R.id.bottomSheetDoneIndicatorIV))
                    .animate()
                    .alpha(1)
                    .rotation(360)
                    .translationY(70 * (getInt(id) + 1))
                    .setListener(new Animator.AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            dismiss();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

        });
    }

    private void setChecked(int i) {
        if (i == 0) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetCopyRadioButton)).setChecked(true);
        } else if (i == 1) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetShareRadioButton)).setChecked(true);
        } else if (i == 2) {
            ((RadioButton) radioGroup.findViewById(R.id.bottomSheetSaveRadioButton)).setChecked(true);
        }
    }

    private int getInt(int id) {
        //Copy -> 0
        //Share -> 1
        //Save -> 2

        if (id == R.id.bottomSheetCopyRadioButton)
            return 0;
        else if (id == R.id.bottomSheetShareRadioButton)
            return 1;
        else if (id == R.id.bottomSheetSaveRadioButton)
            return 2;
        return 1;
    }
}