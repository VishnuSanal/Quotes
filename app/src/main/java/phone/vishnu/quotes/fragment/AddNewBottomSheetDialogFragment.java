package phone.vishnu.quotes.fragment;

import android.app.Application;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.model.Quote;
import phone.vishnu.quotes.repository.FavRepository;

public class AddNewBottomSheetDialogFragment extends BottomSheetDialogFragment {

    private TextInputEditText quoteTIE, authorTIE;
    private Button submitButton, cancelButton;

    public static AddNewBottomSheetDialogFragment newInstance() {
        return new AddNewBottomSheetDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_bottom_sheet_dialog, container, false);

        quoteTIE = view.findViewById(R.id.addNewQuoteTIE);
        authorTIE = view.findViewById(R.id.addNewAuthorTIE);
        submitButton = view.findViewById(R.id.addNewSubmitButton);
        cancelButton = view.findViewById(R.id.addNewCancelButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        submitButton.setOnClickListener(v -> {

            String quote = Objects.requireNonNull(quoteTIE.getText()).toString();
            String author = Objects.requireNonNull(authorTIE.getText()).toString();

            if (quote.isEmpty() || author.isEmpty()) {
                if (quote.isEmpty()) {
                    quoteTIE.setError("Field Empty");
                    quoteTIE.requestFocus();
                } else {
                    authorTIE.setError("Field Empty");
                    authorTIE.requestFocus();
                }
            } else {
                new FavRepository((Application) requireContext().getApplicationContext())
                        .insertFav(new Quote(quote, author, true));

                Toast.makeText(requireContext(), "Quote added to Favourites", Toast.LENGTH_SHORT).show();

                dismiss();
            }

        });

        cancelButton.setOnClickListener(v -> dismiss());

    }
}
