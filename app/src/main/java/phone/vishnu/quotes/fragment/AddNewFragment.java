package phone.vishnu.quotes.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import phone.vishnu.quotes.R;
import phone.vishnu.quotes.helper.FavUtils;
import phone.vishnu.quotes.model.Quote;

public class AddNewFragment extends Fragment {

    private TextInputEditText quoteTIE, authorTIE;
    private Button saveButton, cancelButton;

    public AddNewFragment() {
    }

    public static AddNewFragment newInstance() {
        return new AddNewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_add_new, container, false);

        quoteTIE = inflate.findViewById(R.id.addQuoteTIE);
        authorTIE = inflate.findViewById(R.id.addAuthorTIE);

        saveButton = inflate.findViewById(R.id.buttonAdd);
        cancelButton = inflate.findViewById(R.id.buttonCancel);

        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cancelButton.setOnClickListener(v -> requireActivity().onBackPressed());

        saveButton.setOnClickListener(v -> {

            String quote = Objects.requireNonNull(quoteTIE.getText()).toString();
            String author = Objects.requireNonNull(authorTIE.getText()).toString();

            if (quote.isEmpty() || author.isEmpty()) {
                if (quote.isEmpty()) {
                    quoteTIE.setError("Field Empty");
                    quoteTIE.requestFocus();
                } else if (author.isEmpty()) {
                    authorTIE.setError("Field Empty");
                    authorTIE.requestFocus();
                }
            } else {
                new FavUtils(requireContext()).addFavorite(new Quote(quote, author));

                Toast.makeText(requireContext(), "Quote added to Favourites", Toast.LENGTH_SHORT).show();

                requireActivity().onBackPressed();
            }
        });
    }
}