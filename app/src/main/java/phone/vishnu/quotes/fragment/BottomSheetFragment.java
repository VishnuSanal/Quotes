package phone.vishnu.quotes.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import phone.vishnu.quotes.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetListener listener;
    private ImageView aboutButton, favButton, imgButton, colorButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        aboutButton = v.findViewById(R.id.bottomSheetAbout);
        favButton = v.findViewById(R.id.bottomSheetFav);
        imgButton = v.findViewById(R.id.bottomSheetImageChooser);
        colorButton = v.findViewById(R.id.bottomSheetColorChooser);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBottomSheetButtonClicked(aboutButton.getId());
                dismiss();
            }
        });
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBottomSheetButtonClicked(favButton.getId());
                dismiss();
            }
        });
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBottomSheetButtonClicked(imgButton.getId());
                dismiss();
            }
        });
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBottomSheetButtonClicked(colorButton.getId());
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " had not implemented BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onBottomSheetButtonClicked(int id);
    }
}
