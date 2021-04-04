package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.activity.MainActivity;
import phone.vishnu.quotes.helper.SharedPreferenceHelper;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private Button pickButton, cancelButton;
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

        pickButton = inflate.findViewById(R.id.bottomSheetPickButton);
        cancelButton = inflate.findViewById(R.id.bottomSheetCancelButton);
        radioGroup = inflate.findViewById(R.id.bottomSheetRadioGroup);

        setChecked(sharedPreferenceHelper.getShareButtonAction());

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = radioGroup.getCheckedRadioButtonId();

                sharedPreferenceHelper.setShareButtonAction(getInt(id));

                ((MainActivity) requireActivity()).updateViewPager();

                dismiss();
            }
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